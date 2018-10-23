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

import java.util.Arrays;
import org.jxmpp.jid.BareJid;

/**
 * This class provides static methods to access the Bot configuration.
 * Configuration is a Singleton.
 *
 * @author Dennis Kawurek
 */
public class BotConfiguration {

    private static final BotConfiguration INSTANCE = new BotConfiguration();
    private String jid;
    private char[] pwd;
    private int maxThreads;
    private int queueSize;
    private String configFilePath;
    private String storePath;
    private boolean enableXMPPDebugMode = false;
    private String adminJID;
    private Boolean listenOnlyAdmin;
    private Boolean notifyAdminOnStartup;
    private String trustedStatesFilePath;
    
    private BareJid chatOpenWithJID = null;

    private BotConfiguration() {
        super();
    }

    public static BotConfiguration getInstance() {
        return INSTANCE;
    }

    public void setJID(String jid) {
        if (jid != null) {
            this.jid = jid;
        }
    }

    public void setPassword(char[] password) {
        this.pwd = password;
    }

    public void setMaxThreads(int max_threads) {
        this.maxThreads = max_threads;
    }

    public void setQueueSize(int queue_size) {
        this.queueSize = queue_size;
    }

    public void setStorePath(String storePath) {
        this.storePath = storePath;
    }

    public void setConfigPath(String configFilePath) {
        this.configFilePath = configFilePath;
    }

    public void setEnableXMPPDebugMode(boolean isEnabled) {
        this.enableXMPPDebugMode = isEnabled;
    }
    
    public void setAdminJID(String adminJID) {
        this.adminJID = adminJID;
    }
    
    public void setListenOnlyAdmin(Boolean listenOnlyAdmin) {
        this.listenOnlyAdmin = listenOnlyAdmin;
    }
    
    public void setNotifyAdminOnStartup(Boolean notifyAdminOnStartup) {
        this.notifyAdminOnStartup = notifyAdminOnStartup;
    }
    
    public void setTrustedStatesFilePath(String trustedStatesFilePath) {
        this.trustedStatesFilePath = trustedStatesFilePath;
    }

    public String getJID() {
        return this.jid;
    }

    public char[] getPassword() {
        return this.pwd;
    }

    public int getMaxThreads() {
        return this.maxThreads;
    }

    public int getQueueSize() {
        return this.queueSize;
    }

    public String getStorePath() {
        return this.storePath;
    }

    public boolean getEnableXMPPDebugMode() {
        return this.enableXMPPDebugMode;
    }

    public String getConfigPath() {
        return this.configFilePath;
    }
    
    public String getAdminJID() {
        return this.adminJID;
    }
    
    public boolean getListenOnlyAdmin() {
        return this.listenOnlyAdmin;
    }
    
    public boolean getNotifyAdminOnStartup() {
        return this.notifyAdminOnStartup;
    }
    
    public String getTrustedStatesFilePath() {
        return this.trustedStatesFilePath;
    }
    
    /**
     * Overwrite password array.\n
     * This should be called after initializing the bot (as it is intendend to
     * use the password only for initialization).
     */
    public void clearPassword() {
        Arrays.fill(this.pwd, ' ');
    }

    public void openChat(BareJid jid) {
        this.chatOpenWithJID = jid;
    }

    public void closeChat() {
        this.chatOpenWithJID = null;
    }

    public Boolean isChatOpened() {
        if(null == this.chatOpenWithJID) return false;
        return true;
    }
    
    public BareJid getOpenedChat() {
        if(this.isChatOpened()) return this.chatOpenWithJID;
        return null;
    }
}
