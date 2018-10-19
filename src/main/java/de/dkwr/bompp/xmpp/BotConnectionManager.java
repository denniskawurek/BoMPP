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
package de.dkwr.bompp.xmpp;

import de.dkwr.bompp.util.BotLogger;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.ping.PingManager;

/**
 * This class handles connection messages. It prints when the Bot is connecting
 * to a server, when the connection gets lost and pings the server every
 *
 * @author Dennis Kawurek
 */
public class BotConnectionManager implements ConnectionListener {

    private Thread pingServerThread;
    private final long PING_SERVER_DELAY = 10000;
    private boolean threadSuspended = false;
    private boolean isConnected;

    @Override
    public void connected(XMPPConnection xmppc) {
        System.out.println("Connecting...");
        this.isConnected = true;
    }

    @Override
    public void authenticated(XMPPConnection xmppc, boolean bln) {
        System.out.println("Authenticating...");
        this.threadSuspended = false;
        if (this.pingServerThread == null) {
            PingManager pm = PingManager.getInstanceFor(xmppc);
            this.registerPingFailedListener(pm, xmppc);
            this.pingServerThread = createPingServerThread(pm, xmppc);
            this.pingServerThread.start();
        } else {
            synchronized (this.pingServerThread) {
                this.pingServerThread.notify();
            }
        }
    }

    @Override
    public void connectionClosed() {
        System.out.println("Connection closed... bye");
        isConnected = true;
        this.interuptServerThread();
    }

    @Override
    public void connectionClosedOnError(Exception excptn) {
        System.out.println("Got an error during connection. Trying to reconnect...");
    }

    private void registerPingFailedListener(PingManager pm, XMPPConnection xmppc) {
        pm.registerPingFailedListener(() -> {
            System.out.println("Server Ping failed. Is your connection ok?");
            threadSuspended = true;
        });
    }

    private Thread createPingServerThread(PingManager pm, XMPPConnection xmppc) {
        return new Thread() {
            @Override
            public void run() {
                try {
                    while (xmppc.isConnected() && isConnected) {
                        if (!threadSuspended) {
                            pm.pingMyServer();
                            sleep(PING_SERVER_DELAY);
                        } else {
                            synchronized (this) {
                                while (threadSuspended) {
                                    System.out.println("Waiting for reconnection...");
                                    wait();
                                }
                            }
                        }
                    }
                } catch (SmackException.NotConnectedException | InterruptedException ex) {
                    BotLogger.getInstance().logMsg("Interrupting Ping Thread.");
                }
            }
        };
    }

    private void interuptServerThread() {
        synchronized (this.pingServerThread) {
            this.pingServerThread.interrupt();
        }
    }
}
