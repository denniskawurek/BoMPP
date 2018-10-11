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

import de.dkwr.bompp.util.BotLogger;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.ping.PingManager;

/**
 * This class handles connection messages.
 * It prints when the Bot is connecting to a server, when the connection gets lost and pings the server every 
 * @author Dennis Kawurek
 */
public class XMPPConnectionManager implements ConnectionListener {

    private Thread pingServerThread;
    private final long PING_SERVER_TIME = 3000;

    @Override
    public void connected(XMPPConnection xmppc) {
        System.out.println("Connecting...");
    }

    @Override
    public void authenticated(XMPPConnection xmppc, boolean bln) {
        System.out.println("Authenticating...");
        PingManager pm = PingManager.getInstanceFor(xmppc);
        this.registerPingFailedListener(pm, xmppc);
        this.pingServerThread = createPingServerThread(pm);
        this.pingServerThread.start();
    }

    @Override
    public void connectionClosed() {
        System.out.println("Connection closed... bye");
        this.pingServerThread.interrupt();
    }

    @Override
    public void connectionClosedOnError(Exception excptn) {
        System.out.println("Got an error during connection. Trying to reconnect...");
        this.pingServerThread.interrupt();
    }

    private void registerPingFailedListener(PingManager pm, XMPPConnection xmppc) {
        pm.registerPingFailedListener(() -> {
            System.out.println("Server Ping failed. Is your connection ok?");
            this.pingServerThread.interrupt();
            while (!xmppc.isConnected()) {
                try {
                    Thread.sleep(this.PING_SERVER_TIME);
                } catch (Exception ex) {
                    BotLogger.getInstance().logException(ex);
                }
            }
            this.pingServerThread.start();
        });
    }

    private Thread createPingServerThread(PingManager pm) {
        return new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        pm.pingMyServer();
                        sleep(10000);
                    } catch (Exception ex) {
                        BotLogger.getInstance().logException(ex);
                    }
                }
            }
        };
    }

}
