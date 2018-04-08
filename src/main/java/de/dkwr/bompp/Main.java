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
            String storePath = getStoragePath(args);
            boolean isWindows = isWindows(args);

            BotLogger.getInstance();
            ConfigReader configReader = new ConfigReader(storePath, isWindows);
            configReader.loadConfigFile();
            BotConfiguration cfg = BotConfiguration.getInstance();
            
            CommandQueue commandQueue = new CommandQueue(cfg.getMaxThreads(), cfg.getQueueSize());

            BotInitializer botInitializer = new BotInitializer();
            botInitializer.init(cfg.getJID(), cfg.getPassword(), storePath, commandQueue);
            cfg.clearPassword();

            CommandHandler botCommandHandler = new BotCommandHandler(botInitializer.getOmemoController(), configReader, commandQueue);
            new BotControlThread(botCommandHandler).run();
        } catch (Exception ex) {
            BotLogger.getInstance().logException(ex);
        }
    }

    private static String getStoragePath(String[] args) {
        if (args.length < 2) {
            printHelp();
            System.exit(0);
        } else if (args.length >= 1) { // if storePath is or is not initialized take args
            for(int i = 0; i <= args.length-1; i++) {
                if(args[i].equalsIgnoreCase("-p") && i < args.length-1) {
                    return args[i+1];
                }
            }
        } else {
            printHelp();
            System.exit(0);
        }
        return null; //should never reach this
    }

    private static boolean isWindows(String[] args) {
        if(args.length < 2) {
            return false;
        } else if(args.length >= 1) {
            for(int i = 0; i <= args.length-1; i++) {
                if(args[i].equalsIgnoreCase("-w")) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void printHelp() {
        String listFmt = "%-25s%-25s";
        System.out.println("usage: -p STORAGE_PATH [-w]");
        System.out.println(String.format(listFmt, "-p STORAGE_PATH", "Absolute path of the storage"));
        System.out.println(String.format(listFmt, "-w", "Used platform is windows"));
    }
}
