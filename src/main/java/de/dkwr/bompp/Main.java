package de.dkwr.bompp;

import de.dkwr.bompp.commandexecutor.CommandQueue;
import de.dkwr.bompp.commandhandler.BotCommandHandler;
import de.dkwr.bompp.commandhandler.CommandHandler;
import de.dkwr.bompp.util.BotLogger;
import de.dkwr.bompp.util.ConfigReader;
import de.dkwr.bompp.util.BotConfiguration;

/**
 * Main class and entry point for the bot.
 *
 * @author Dennis Kawurek
 */
public class Main {
    public static void main(String[] args) {
        try {
            String storePath = ""; // if is != null, take this as store path otherwise args[0]
            if (args.length < 1 && storePath == null) {
                throw new IllegalArgumentException("You must call the bot with one argument, which is the absolute path to the store folder.");
            } else if (args.length > 1 && storePath == null) { // variable storePath is not initialized, so take argument
                storePath = args[0];
            }
            BotLogger.getInstance();
            ConfigReader configReader = new ConfigReader(storePath);
            configReader.loadConfigFile();
            BotConfiguration cfg = BotConfiguration.getInstance();
            
            CommandQueue commandQueue = new CommandQueue(cfg.getMaxThreads(), cfg.getQueueSize());

            BotInitializer botInitializer = new BotInitializer();
            botInitializer.init(cfg.getJID(), cfg.getPassword(), storePath, configReader.getCmdList(), commandQueue);
            cfg.clearPassword();

            CommandHandler botCommandHandler = new BotCommandHandler(botInitializer.getOmemoController(), configReader, commandQueue);
            new BotControlThread(botCommandHandler).run();
        } catch (Exception ex) {
            BotLogger.getInstance().logException(ex);
        }
    }
}
