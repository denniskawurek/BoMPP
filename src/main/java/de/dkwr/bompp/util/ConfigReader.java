/*
 * Copyright (C) 2018 Dennis Kawurek
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.dkwr.bompp.util;

import java.io.Console;
import java.io.File;
import java.io.FileReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * This class provides methods to read the config file which lays in the store
 * folder.
 *
 * @author Dennis Kawurek
 */
public class ConfigReader {

    private final String storePath;
    private final String configName = "config.json";
    private final String trustedStatesFileName = ".trustedStates";
    private final String configFilePath;
    private final CommandList cmdList;

    // Config file keys
    private final String BOT_KEY = "bot";
    private final String CMDS_KEY = "cmds";
    private final String JID_KEY = "jid";
    private final String PWD_KEY = "pwd";
    private final String MAX_THREADS_KEY = "max_threads";
    private final String QUEUE_SIZE_KEY = "queue_size";
    private final String ADMIN_JID_KEY = "admin_jid";
    private final String LISTEN_ONLY_ADMIN_KEY = "listen_only_admin";
    private final String CMD_KEY = "cmd";
    private final String DESCRIPTION_KEY = "description";
    private final String EXEC_TYPE_KEY = "exec_type";
    private final String SCRIPT_KEY = "script";
    private final String ENABLE_XMPP_DEBUG = "enable_xmpp_debug";
    private final String NOTIFY_ADMIN_ON_STARTUP = "notify_admin_on_startup";

    /**
     * Creates an object of the ConfigReader and reads the config file.
     *
     * @param path path of the configuration file. The configuration file has to be a JSON
     * @param fileSeparator the filesepartaor of the used OS
     */
    public ConfigReader(String path, String fileSeparator) {
        this.storePath = path + fileSeparator;
        this.configFilePath = this.storePath + this.configName;
        this.cmdList = CommandList.getInstance();
    }

    public void loadConfigFile() throws Exception {
        JSONParser parser = new JSONParser();

        if (!this.pathExists() || !this.configFileExists()) {
            throw new IllegalArgumentException("The given path of the store doesn't exists or there is no config file in the store: " + this.configFilePath);
        }
        try {
            System.out.println(this.configFilePath);
            Object obj = parser.parse(new FileReader(this.configFilePath));

            JSONObject jsonConfig = (JSONObject) obj;
            JSONObject botConfig = (JSONObject) jsonConfig.get(this.BOT_KEY);
            JSONArray cmdArr = (JSONArray) jsonConfig.get(this.CMDS_KEY);

            BotConfiguration cfg = BotConfiguration.getInstance();
            cfg.setStorePath(this.storePath);
            cfg.setConfigPath(this.configFilePath);

            this.checkConfigFile(botConfig);

            cfg.setJID((String) botConfig.get(this.JID_KEY));
            
            cfg.setMaxThreads(Integer.parseInt((String) botConfig.get(this.MAX_THREADS_KEY)));
            cfg.setQueueSize(Integer.parseInt((String) botConfig.get(this.QUEUE_SIZE_KEY)));
            cfg.setAdminJID((String) botConfig.get(this.ADMIN_JID_KEY));
            cfg.setListenOnlyAdmin((Boolean) botConfig.get(this.LISTEN_ONLY_ADMIN_KEY));
            cfg.setNotifyAdminOnStartup((Boolean) botConfig.get(this.NOTIFY_ADMIN_ON_STARTUP));
            cfg.setTrustedStatesFilePath(this.storePath + this.trustedStatesFileName);
            
            if(botConfig.get(this.PWD_KEY) != null) {
                cfg.setPassword(((String) botConfig.get(this.PWD_KEY)).toCharArray());
            } else {
                cfg.setPassword(this.readPasswordFromConsole());
            }
            
            if(botConfig.get(this.ENABLE_XMPP_DEBUG) != null) {
                cfg.setEnableXMPPDebugMode((Boolean) botConfig.get(this.ENABLE_XMPP_DEBUG));
            }

            for (int i = 0; i < cmdArr.size(); i++) {
                JSONObject cmdObj = (JSONObject) cmdArr.get(i);

                String cmd = (String) cmdObj.get(this.CMD_KEY);
                String description = (String) cmdObj.get(this.DESCRIPTION_KEY);
                String exec_type = (String) cmdObj.get(this.EXEC_TYPE_KEY);
                String script = (String) cmdObj.get(this.SCRIPT_KEY);

                if (this.cmdList.cmdExists(cmd)) {
                    System.out.println("Error: Found multiple command " + cmd + "!\n"
                            + "Added the first occurence. Please check your config file.");
                } else {
                    this.cmdList.addCommand(cmd, script, exec_type, description);
                }
            }

            System.out.println("Loaded config file with " + this.cmdList.getSize() + " commands");
        } catch (Exception ex) {
            BotLogger.getInstance().logException(ex);
            System.exit(0);
        }
    }

    /**
     * Reloads the config file, so the user doesn't have to restart the bot
     * after changing it.
     */
    public void reloadConfigFile() throws Exception {
        this.cmdList.clear();
        this.loadConfigFile();
    }

    private void checkConfigFile(JSONObject botConfig) {
        // checks if the config file has all required fields
        if(botConfig.get(this.JID_KEY) == null
                || botConfig.get(this.MAX_THREADS_KEY) == null
                || botConfig.get(this.QUEUE_SIZE_KEY) == null
                || botConfig.get(this.ADMIN_JID_KEY) == null
                || botConfig.get(this.LISTEN_ONLY_ADMIN_KEY) == null
                || botConfig.get(this.NOTIFY_ADMIN_ON_STARTUP) == null) {
            throw new IllegalArgumentException("Error while loading the config file. " +
                    "Please take a look at the documentation to see which fields are required.");
        }
        
        // if there is a password in the config file set, print a hint.
        if(botConfig.get(this.PWD_KEY) != null) {
            System.out.println("\u001B[33mWarning: You've set a password in your config file, which is a security risk.\n"
                    + "If possible you should remove the field from your config and use the command line instead.\u001B[0m");
        }
    }

    private boolean pathExists() {
        File f = new File(this.storePath);
        if (f.isDirectory()) {
            return true;
        } else if (!f.isDirectory() || !f.exists()) {
            return false;
        }
        return false; // shouldn't happen
    }

    private boolean configFileExists() {
        File f = new File(this.configFilePath);
        return f.exists();
    }
    
    private char[] readPasswordFromConsole() {
        Console console = System.console();
        if(console == null) {
            BotLogger.getInstance().logMsg("Error in console declaration. Please run this program from console.\n"
                    + "If you are running from an IDE you should set your password in the config file and remove it later.");
            System.exit(1);
        }
        return console.readPassword("Enter password for server JID:\n");
    }
}
