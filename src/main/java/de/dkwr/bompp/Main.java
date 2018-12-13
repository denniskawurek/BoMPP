package de.dkwr.bompp;

import de.dkwr.bompp.cmd.exec.CommandQueue;
import de.dkwr.bompp.cmd.handler.BotCommandHandler;
import de.dkwr.bompp.util.BotLogger;
import de.dkwr.bompp.util.ConfigReader;
import de.dkwr.bompp.util.BotConfiguration;
import de.dkwr.bompp.util.ConfigFileWatcher;
import org.jxmpp.jid.BareJid;

/**
 * Main class and entry point for the bot.
 *
 * @author Dennis Kawurek
 */
public class Main {
    public static void main(String[] args) {
        try {
            String storePath = getStoragePath(args);
            String fileSeparator = getFileSeparator();

            ConfigReader configReader = new ConfigReader(storePath, fileSeparator);
            configReader.loadConfigFile();
            BotConfiguration cfg = BotConfiguration.getInstance();

            CommandQueue commandQueue = new CommandQueue(cfg.getMaxThreads(), cfg.getQueueSize());

            BotInitializer botInitializer = new BotInitializer();
            botInitializer.init(cfg, commandQueue);
            cfg.clearPassword();
            
            ConfigFileWatcher cfgWatcher = new ConfigFileWatcher(configReader);
            cfgWatcher.watch();
            
            BareJid adminJID = botInitializer.getOmemoController().getJid(cfg.getAdminJID());
            BotCommandHandler botCommandHandler = new BotCommandHandler(botInitializer.getOmemoController(), configReader, commandQueue, botInitializer.getScriptCommandHandler(), adminJID);
            
            if(cfg.getNotifyAdminOnStartup())
                botCommandHandler.sendAdminMessage("Bot started!");
            
            new BotControlThread(botCommandHandler).run();
        } catch (Exception ex) {
            BotLogger.getInstance().logException(ex);
        }
    }

    private static String getStoragePath(String[] args) {
        if (args.length < 2) {
            System.out.println("<");
            printHelp();
            System.exit(0);
        } else if (args.length >= 1) { // if storePath is or is not initialized take args
            for (int i = 0; i <= args.length - 1; i++) {
                if (args[i].equalsIgnoreCase("-p") && i < args.length - 1) {
                    return args[i + 1];
                }
            }
        } else {
            System.out.println("<");
            printHelp();
            System.exit(0);
        }
        return null; //should never reach this
    }

    private static String getFileSeparator() {
        return System.getProperties().getProperty("file.separator");
    }

    private static void printHelp() {
        String listFmt = "%-25s%-25s";
        System.out.println("usage: -p STORAGE_PATH");
        System.out.println(String.format(listFmt, "-p STORAGE_PATH", "Absolute path of the storage"));
    }
}
