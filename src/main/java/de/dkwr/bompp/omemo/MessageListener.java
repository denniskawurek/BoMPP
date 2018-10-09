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
package de.dkwr.bompp.omemo;

import de.dkwr.bompp.cmd.handler.CommandHandler;
import de.dkwr.bompp.util.BotConfiguration;
import de.dkwr.bompp.util.BotLogger;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.carbons.packet.CarbonExtension;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.omemo.OmemoMessage;
import org.jivesoftware.smackx.omemo.internal.OmemoDevice;
import org.jivesoftware.smackx.omemo.listener.OmemoMessageListener;
import org.jivesoftware.smackx.omemo.listener.OmemoMucMessageListener;

/**
 * Provides all message listeners for the bot.
 *
 * @author Dennis Kawurek
 */
public class MessageListener {

    private CommandHandler commandHandler;

    public MessageListener(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    /**
     * Returns an Listener which listens for omemo decrypted messages by a user.
     *
     * @return an MessageListener of the type OmemoMessageListener
     */
    public OmemoMessageListener setupOmemoMessageListener() {
        return new OmemoMessageListener() {
            @Override
            public void onOmemoMessageReceived(Stanza stanza, OmemoMessage.Received rcvd) {
                OmemoDevice senderDevice = rcvd.getSenderDevice();
                String sender = senderDevice.getJid().asUnescapedString();
                String decryptedBody = rcvd.getBody();
                BotConfiguration cfg = BotConfiguration.getInstance();
                if (sender != null && decryptedBody != null) {
                    if ((cfg.getListenOnlyAdmin() && cfg.getAdminJID().equalsIgnoreCase(sender))
                            || !cfg.getListenOnlyAdmin()) {
                        System.out.println("\033[34m" + sender + ": " + decryptedBody + "\033[0m ");
                        commandHandler.handleCommand(decryptedBody, sender);
                    } else {
                        BotLogger.getInstance().logMsg("Got message by other user than administrator:\n"
                                + "" + sender + ": " + decryptedBody);
                    }
                }
            }

            @Override
            public void onOmemoCarbonCopyReceived(CarbonExtension.Direction drctn, Message msg, Message msg1, OmemoMessage.Received rcvd) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
    }

    /**
     * Returns an MessageListeners which listens for Multi-user Chat messages.
     *
     * @return MessageListener of type OmemoMucMessageListener
     */
    public OmemoMucMessageListener setupOmemoMucMessageListener() {
        return (MultiUserChat muc, Stanza stanza, OmemoMessage.Received rcvd) -> {
            /*if (multiUserChat != null && bareJid != null && s != null) {
            System.out.println("\033[36m" + multiUserChat.getRoom() + ": " + bareJid + ": " + s + "\033[0m " + (omemoMessageInformation != null ? omemoMessageInformation : ""));
            }*/
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        };
    }
}
