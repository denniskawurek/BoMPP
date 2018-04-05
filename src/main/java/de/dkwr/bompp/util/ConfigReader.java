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

    private final String path;
    private final String configName = "config.json";
    private final String configFilePath;
    private String jid;
    private String pwd;
    private final CommandList cmdList;
    private int max_threads;
    private int queue_size;

    /**
     * Creates an object of the ConfigReader and reads the config file.
     *
     * @param path
     */
    public ConfigReader(String path) {
        this.path = path;
        this.configFilePath = this.path + "/" + this.configName;
        this.cmdList = new CommandList();
    }

    public void loadConfigFile() throws Exception {
        JSONParser p = new JSONParser();
        JSONParser parser = new JSONParser();

        if (!this.pathExists() || !this.configFileExists()) {
            throw new IllegalArgumentException("The given path of the store doesn't exists or there is no config file in the store.");
        }
        try {
            Object obj = parser.parse(new FileReader(this.configFilePath));

            JSONObject jsonConfig = (JSONObject) obj;
            JSONObject botConfig = (JSONObject) jsonConfig.get("bot");
            JSONArray cmdList = (JSONArray) jsonConfig.get("cmds");

            this.jid = (String) botConfig.get("jid");
            this.pwd = (String) botConfig.get("pwd");
            this.max_threads = Integer.parseInt((String) botConfig.get("max_threads"));
            this.queue_size = Integer.parseInt((String) botConfig.get("queue_size"));

            for (int i = 0; i < cmdList.size(); i++) {
                JSONObject cmdObj = (JSONObject) cmdList.get(i);

                String cmd = (String) cmdObj.get("cmd");
                String description = (String) cmdObj.get("description");
                String type = (String) cmdObj.get("type");
                String script = (String) cmdObj.get("script");

                if (this.cmdList.cmdExists(cmd)) {
                    System.out.println("Error: Found multiple command " + cmd + "!\n"
                            + "Added the first occurence. Please check your config file.");
                } else {
                    this.cmdList.addCommand(cmd, script, type, description);
                }
            }

            System.out.println("Loaded config file with " + this.cmdList.getSize() + " commands");
        } catch (Exception ex) {
            BotLogger.logException(ex);
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

    /**
     * Returns the command list.
     * @return Object of type CommandList
     */
    public CommandList getCmdList() {
        return this.cmdList;
    }

    /**
     * Sets the variable password to null, so it will not be on the heap
     * anymore.<br/>
     * This should be called after initializing the bot (as it is intendend to
     * use the password only for initialization).
     */
    public void clearPassword() {
        this.pwd = null;
    }

    public String getJID() {
        return this.jid;
    }

    public String getPassword() {
        return this.pwd;
    }

    public String getStorePath() {
        return this.path;
    }
    
    public int getMaxThreads() {
        return this.max_threads;
    }
    
    public int getQueueSize() {
        return this.queue_size;
    }

    private boolean pathExists() {
        File f = new File(this.path);

        if (f.isDirectory()) {
            return true;
        } else if (!f.isDirectory() || !f.exists()) {
            return false;
        }
        return false; // shouldn't happen
    }

    private boolean configFileExists() {
        File f = new File(this.configFilePath);

        if (f.exists()) {
            return true;
        }
        return false;
    }
}
