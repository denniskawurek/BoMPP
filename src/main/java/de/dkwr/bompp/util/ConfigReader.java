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

    private final String storePath;
    private final String configName = "config.json";
    private final String configFilePath;
    private final CommandList cmdList;
    /**
     * Creates an object of the ConfigReader and reads the config file.
     *
     * @param path
     */
    public ConfigReader(String path, boolean isWindows) {
        this.storePath = path;
        if(isWindows) {
            this.configFilePath = this.storePath + "\\" + this.configName;
        } else {
            this.configFilePath = this.storePath + "/" + this.configName;
        }
        this.cmdList = CommandList.getInstance();
    }

    public void loadConfigFile() throws Exception {
        JSONParser p = new JSONParser();
        JSONParser parser = new JSONParser();

        if (!this.pathExists() || !this.configFileExists()) {
            throw new IllegalArgumentException("The given path of the store doesn't exists or there is no config file in the store: " + this.configFilePath);
        }
        try {
            Object obj = parser.parse(new FileReader(this.configFilePath));

            JSONObject jsonConfig = (JSONObject) obj;
            JSONObject botConfig = (JSONObject) jsonConfig.get("bot");
            JSONArray cmdArr = (JSONArray) jsonConfig.get("cmds");
            
            BotConfiguration cfg = BotConfiguration.getInstance();
            cfg.setJID((String) botConfig.get("jid"));
            cfg.setPassword((String) botConfig.get("pwd"));
            cfg.setMaxThreads(Integer.parseInt((String) botConfig.get("max_threads")));
            cfg.setQueueSize(Integer.parseInt((String) botConfig.get("queue_size")));
            cfg.setStorePath(this.storePath);
            cfg.setConfigPath(this.configFilePath);

            for (int i = 0; i < cmdArr.size(); i++) {
                JSONObject cmdObj = (JSONObject) cmdArr.get(i);

                String cmd = (String) cmdObj.get("cmd");
                String description = (String) cmdObj.get("description");
                String exec_type = (String) cmdObj.get("exec_type");
                String script = (String) cmdObj.get("script");

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

    public String getStorePath() {
        return this.storePath;
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
        if (f.exists()) {
            return true;
        }
        return false;
    }
}
