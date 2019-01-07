/*
 * Copyright (C) 2019 Dennis Kawurek
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
package de.dkwr.bompp.cmd.handler.bot;

import java.util.Optional;

/**
 * To create a bot command to exec through the CLI, you need to implement this
 * class and initialize it in the BotCommandHandler class.
 *
 * @author Dennis Kawurek
 */
public abstract class AbstractBotCommand {

    private final String command;
    private final String commandDescription;
    private int paramLength = -1;
    private final String usage;

    /**
     * Constructor for a command without needed min number of params.
     *
     * @param command the command String to call
     * @param usage description of params and their usage
     * @param commandDescription the command description (printed for help)
     */
    public AbstractBotCommand(String command, Optional<String> usage, String commandDescription) {
        this.command = command;
        this.commandDescription = commandDescription;
        
        if(usage.isPresent()) this.usage = usage.get();
        else this.usage = "";
    }

    /**
     * Constructor for a command which needs a minimal amount of params.
     *
     * @param command the command String to call
     * @param usage description of params and their usage
     * @param commandDescription the command description (printed for help)
     * @param paramLength number of needed params
     */
    public AbstractBotCommand(String command, Optional<String> usage, String commandDescription, int paramLength) {
        this(command, usage, commandDescription);
        this.paramLength = paramLength;
    }

    /**
     * Checks if number of params are valid.
     *
     * @param params
     * @return
     */
    public boolean paramsNumberValid(Optional<String> params) {
        if (this.paramLength <= 0) {
            return true;
        }

        if (!params.isPresent() && this.paramLength > 0) {
            return false;
        }

        String args = params.get();

        if (this.paramLength == 1 && args.contains(" ")) {
            return false;
        }

        if (this.paramLength > 1 && !args.contains(" ")) {
            return false;
        }

        return true;
    }

    /**
     * Execution of the command.
     *
     * @param cmd name of command
     * @param params params for the execution of commands
     * @return true if execution successfull, false if it fails
     * @throws IllegalArgumentException when the arguments are false
     */
    public abstract boolean exec(String cmd, Optional<String> params) throws IllegalArgumentException;

    /**
     * Returns the command String.
     *
     * @return a String of the command.
     */
    public String getCommand() {
        return this.command;
    }

    /**
     * Prints the usage of the
     *
     * @return a String which shows how to call the command.
     */
    public String usage() {
        StringBuilder callStr = new StringBuilder();
        callStr.append(this.command)
                .append(" ")
                .append(this.usage);
        
        String listFmt = "%-30s%s\n";
        return String.format(listFmt, callStr, this.commandDescription);
    }
}
