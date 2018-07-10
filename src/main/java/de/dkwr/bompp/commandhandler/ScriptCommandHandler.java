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
package de.dkwr.bompp.commandhandler;

import de.dkwr.bompp.commandexecutor.CommandQueue;
import de.dkwr.bompp.commandexecutor.ExecuteScriptThread;
import de.dkwr.bompp.omemo.OmemoController;
import de.dkwr.bompp.util.BotLogger;
import de.dkwr.bompp.util.CommandList;

/**
 * This CommandHandler is responsible for the execution of scripts after getting
 * a remote command.<br/>Basically he reads the config.json, makes a lookup if
 * the command and script exists, executes it and communicates with the
 * executing XMPP/Jabber account.
 *
 * @author Dennis Kawurek
 */
public class ScriptCommandHandler implements CommandHandler {

    private OmemoController omemoController;
    private final CommandList commandList;
    private final CommandQueue commandQueue;
    private final String COMMANDS_AVAILABLE_STR = "These commands are available:\n";

    /**
     * Creates a new ScriptCommandHandler object
     * @param commandQueue the commandQueue of this session
     */
    public ScriptCommandHandler(CommandQueue commandQueue) {
        this.commandList = CommandList.getInstance();
        this.commandQueue = commandQueue;
    }

    @Override
    public void setOmemoController(OmemoController omemoController) {
        this.omemoController = omemoController;
    }

    @Override
    public void handleCommand(String cmd) {
        try {
            cmd = cmd.toLowerCase();
            if (this.commandList.cmdExists(cmd)) {
                String[] cmdAsArr = this.convertCmdToArr(cmd);

                ExecuteScriptThread executeScriptThread = new ExecuteScriptThread(cmdAsArr, null, true, this.omemoController);
                this.commandQueue.addToQueue(executeScriptThread);
            } else if(cmd.equalsIgnoreCase("help")) {
                System.out.println(this.COMMANDS_AVAILABLE_STR + this.getAllCommandsAsString());
            } else {
                System.out.println("This command doesn't exist.");
                System.out.println(this.COMMANDS_AVAILABLE_STR + this.getAllCommandsAsString());
            }
        } catch (Exception ex) {
            BotLogger.getInstance().logException(ex);
        }
    }

    @Override
    public void handleCommand(String cmd, String clientJID) {
        try {
            cmd = cmd.toLowerCase();
            if (this.commandList.cmdExists(cmd)) {
                String[] cmdAsArr = this.convertCmdToArr(cmd);

                ExecuteScriptThread executeScriptThread = new ExecuteScriptThread(cmdAsArr, clientJID, true, this.omemoController);
                this.commandQueue.addToQueue(executeScriptThread);
            } else if(cmd.equalsIgnoreCase("help")) {
                this.omemoController.sendMessage(clientJID,  this.COMMANDS_AVAILABLE_STR + this.getAllCommandsAsString());
            } else {
                this.omemoController.sendMessage(clientJID, "This command doesn't exist.");
                this.omemoController.sendMessage(clientJID, this.COMMANDS_AVAILABLE_STR + this.getAllCommandsAsString());
            }
        } catch (Exception ex) {
            BotLogger.getInstance().logException(ex);
        }
    }

    private String[] convertCmdToArr(String cmd) {
        String[] cmdAsArr = cmd.split(" ");
        String[] cmdDetails = this.commandList.getCommand(cmdAsArr[0]);
        String scriptPath = cmdDetails[0];
        String execType = cmdDetails[1];
        cmdAsArr[0] = scriptPath; // replaces the command with the path

        if(!execType.isEmpty()) {
            String[] tempArr = new String[cmdAsArr.length+1];
            System.arraycopy(cmdAsArr, 0, tempArr, 1, cmdAsArr.length);
            tempArr[0] = execType;
            cmdAsArr = tempArr;
        }

        return cmdAsArr;
    }

    @Override
    public String getAllCommandsAsString() {
        return this.commandList.toString();
    }

}
