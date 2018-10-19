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
package de.dkwr.bompp.cmd.exec;

import de.dkwr.bompp.xmpp.OmemoController;
import de.dkwr.bompp.util.BotLogger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/**
 * The ExecuteScriptThread runs a script in a Thread and notifies the client when it is terminated.
 *
 * @author Dennis Kawurek
 */
public class ExecuteScriptThread implements Runnable {
    private boolean showOutPutStream = true;
    List<String> paramList;
    private final String clientJID;
    private final OmemoController omemoController;

    public ExecuteScriptThread(String[] execArr, String clientJID, boolean showOutPutStream, OmemoController omemoController) {
        this.clientJID = clientJID;
        this.paramList = Arrays.asList(execArr);
        this.showOutPutStream = showOutPutStream;
        this.omemoController = omemoController;
    }

    @Override
    public void run() {
        try {
            System.out.println("Executing " + paramList.get(0) + " for " + clientJID);
            Process exec;
            exec = new ProcessBuilder(paramList).start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(exec.getInputStream())
            );
            String s;
            StringBuilder strBuilder = new StringBuilder();
            while (exec.isAlive()) {
                if (this.showOutPutStream) {
                    while ((s = reader.readLine()) != null) {
                        System.out.println(s);
                        strBuilder.append(s);
                        strBuilder.append(System.getProperty("line.separator"));
                    }
                }
            }

            if(this.clientJID != null) {
                System.out.println("End of execution of " + paramList.get(0) + " for " + clientJID + " Exit code: " + exec.exitValue());
                this.omemoController.sendMessage(this.omemoController.getJid(this.clientJID), strBuilder.toString());
            } else {
                System.out.println("End of execution of " + paramList.get(0) + " Exit code: " + exec.exitValue());
            }
        } catch (Exception ex) {
            BotLogger.getInstance().logException(ex);
            if(this.clientJID != null) {
                try {
                    this.omemoController.sendMessage(this.omemoController.getJid(this.clientJID), "Failed to execute the command " + paramList.get(0) + "\nPlease try it again.");
                } catch (Exception e) {
                    BotLogger.getInstance().logException(e);
                }
            }
        }
    }

}
