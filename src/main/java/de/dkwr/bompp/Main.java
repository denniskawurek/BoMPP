package de.dkwr.bompp;

import commandexecutor.CommandQueue;
import de.dkwr.bompp.commandhandler.BotCommandHandler;
import de.dkwr.bompp.commandhandler.CommandHandler;
import de.dkwr.bompp.commandhandler.ScriptCommandHandler;
import de.dkwr.bompp.util.BotLogger;
import de.dkwr.bompp.util.ConfigReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smackx.omemo.OmemoManager;

/**
 * Main class and entry point for the bot.
 *
 * @author Dennis Kawurek
 */
public class Main {
    public static void main(String[] args) {
        try {
            String storePath = "/home/dennis/bompp_store"; // if is != null, take this as store path otherwise args[0]
            if (args.length < 1 && storePath == null) {
                throw new IllegalArgumentException("You must call the bot with one argument, which is the absolute path to the store folder.");
            } else if (args.length > 1 && storePath == null) { // variable storePath is not initialized, so take argument
                storePath = args[0];
            }

            ConfigReader configReader = new ConfigReader(storePath);
            configReader.loadConfigFile();
            
            CommandQueue commandQueue = new CommandQueue(configReader.getMaxThreads(), configReader.getQueueSize());

            BotInitializer botInitializer = new BotInitializer();
            botInitializer.init(configReader.getJID(), configReader.getPassword(), storePath, configReader.getCmdList(), commandQueue);
            configReader.clearPassword();

            CommandHandler botCommandHandler = new BotCommandHandler(botInitializer.getOmemoController(), configReader, commandQueue);
            new BotControlThread(botCommandHandler).run();
        } catch (Exception ex) {
            BotLogger.logException(ex);
        }
    }
}
