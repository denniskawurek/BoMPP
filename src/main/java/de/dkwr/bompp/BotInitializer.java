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
package de.dkwr.bompp;

import de.dkwr.bompp.cmd.exec.CommandQueue;
import de.dkwr.bompp.cmd.handler.CommandHandler;
import de.dkwr.bompp.cmd.handler.ScriptCommandHandler;
import de.dkwr.bompp.xmpp.BotTrustCallback;
import de.dkwr.bompp.util.BotLogger;
import de.dkwr.bompp.xmpp.MessageListener;
import de.dkwr.bompp.xmpp.OmemoController;
import de.dkwr.bompp.xmpp.BotConnectionManager;
import de.dkwr.bompp.util.BotConfiguration;

import java.io.File;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.carbons.CarbonManager;
import org.jivesoftware.smackx.omemo.OmemoConfiguration;
import org.jivesoftware.smackx.omemo.OmemoManager;
import org.jivesoftware.smackx.omemo.OmemoService;
import org.jivesoftware.smackx.omemo.OmemoStore;
import org.jivesoftware.smackx.omemo.listener.OmemoMessageListener;
import org.jivesoftware.smackx.omemo.listener.OmemoMucMessageListener;
import org.jivesoftware.smackx.omemo.signal.SignalCachingOmemoStore;
import org.jivesoftware.smackx.omemo.signal.SignalFileBasedOmemoStore;
import org.jivesoftware.smackx.omemo.signal.SignalOmemoService;

/**
 * The BotInitializer creates an XMPP Connection and initializes all objects to listen for Omemo Messages.
 * @author Dennis Kawurek
 */
public class BotInitializer {
    private AbstractXMPPConnection connection;
    private OmemoManager omemoManager;
    private OmemoStore omemoStore;
    private OmemoMessageListener omemoMessageListener;
    private OmemoMucMessageListener omemoMucMessageListener;
    private OmemoController omemoController;
    private ChatManager chatManager;
    private Roster roster;
    private CommandHandler scriptCommandHandler;

    /**
     * Initializes the XMPP connection and OMEMOManager for the bot with all
     * message listeners.
     * @param cfg Configuration for the bot
     * @param commandQueue the CommandQueue where the commands belong to
     */
    public void init(BotConfiguration cfg, CommandQueue commandQueue) {
        try {
            System.out.println("Start initializing BoMPP please wait...");
            SmackConfiguration.DEBUG = cfg.getEnableXMPPDebugMode();
            OmemoConfiguration.setAddOmemoHintBody(false);

            this.connection = new XMPPTCPConnection(cfg.getJID(), new String(cfg.getPassword()));
            this.connection.addConnectionListener(new BotConnectionManager());
            
            ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(this.connection);
            reconnectionManager.enableAutomaticReconnection();
            reconnectionManager.setReconnectionPolicy(ReconnectionManager.ReconnectionPolicy.RANDOM_INCREASING_DELAY);
            
            SignalOmemoService.acknowledgeLicense();
            SignalOmemoService.setup();
            SignalOmemoService service = (SignalOmemoService) SignalOmemoService.getInstance();
            service.setOmemoStoreBackend(new SignalCachingOmemoStore(new SignalFileBasedOmemoStore(new File(cfg.getStorePath()))));
            this.omemoManager = OmemoManager.getInstanceFor(connection);
            this.omemoManager.setTrustCallback(new BotTrustCallback());
            
            this.omemoStore = OmemoService.getInstance().getOmemoStoreBackend();
            
            this.connection.setReplyTimeout(30000);
            this.connection = connection.connect();
            this.connection.login();
            
            this.omemoManager.initialize();

            CarbonManager.getInstanceFor(connection).enableCarbons();

            this.roster = Roster.getInstanceFor(connection);
            this.roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
            
            this.scriptCommandHandler = new ScriptCommandHandler(commandQueue);
            MessageListener messageListener = new MessageListener(scriptCommandHandler);
            this.omemoManager.addOmemoMessageListener(messageListener.setupOmemoMessageListener());
            this.omemoManager.addOmemoMucMessageListener(messageListener.setupOmemoMucMessageListener());
            
            this.chatManager = ChatManager.getInstanceFor(connection);
            
            this.omemoController = new OmemoController(this.connection, this.omemoManager, this.omemoStore, this.roster, this.chatManager);
            
            this.scriptCommandHandler.setOmemoController(this.omemoController);
            
            System.out.println("OMEMO setup complete and BoMPP started!");
        } catch (Exception ex) {
            BotLogger.getInstance().logException(ex);
            System.out.println("Connection failed. Is your password ok?\nQuit.");
            System.exit(-1);
        }
    }
    
    /**
     * Returns the OmemoController for the connection.
     * @return an OmemoController
     */
    public OmemoController getOmemoController() {
        return this.omemoController;
    }

    public CommandHandler getScriptCommandHandler() {
        return this.scriptCommandHandler;
    }
}
