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

/**
 * This class provides static methods to access the Bot configuration.
 * Configuration is a Singleton.
 *
 * @author Dennis Kawurek
 */
public class BotConfiguration {

    private static final BotConfiguration instance = new BotConfiguration();
    private String jid;
    private String pwd;
    private int max_threads;
    private int queue_size;
    private String configFilePath;
    private String storePath;

    private BotConfiguration() {
        super();
    }

    public static BotConfiguration getInstance() {
        return instance;
    }

    public void setJID(String jid) {
        if (jid != null) {
            this.jid = jid;
        }
    }

    public void setPassword(String password) {
        this.pwd = password;
    }

    public void setMaxThreads(int max_threads) {
        this.max_threads = max_threads;
    }

    public void setQueueSize(int queue_size) {
        this.queue_size = queue_size;
    }

    public void setStorePath(String storePath) {
        this.storePath = storePath;
    }

    public void setConfigPath(String configFilePath) {
        this.configFilePath = configFilePath;
    }

    public String getJID() {
        return this.jid;
    }

    public String getPassword() {
        return this.pwd;
    }

    public int getMaxThreads() {
        return this.max_threads;
    }

    public int getQueueSize() {
        return this.queue_size;
    }

    public String getStorePath() {
        return this.storePath;
    }

    public String getConfigPath() {
        return this.configFilePath;
    }

    /**
     * Sets the variable of the password to null, so it will not be on the heap
     * anymore.<br/>
     * This should be called after initializing the bot (as it is intendend to
     * use the password only for initialization).
     */
    public void clearPassword() {
        this.pwd = null;
    }
}
