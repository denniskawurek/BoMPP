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

import de.dkwr.bompp.commandhandler.CommandHandler;
import de.dkwr.bompp.util.StaticScanner;
import java.util.Scanner;

/**
 * The BotControlThread is responsible for reading commands from the bot administator CLI and for the exceution of the bot specific commands.
 *
 * @author Dennis Kawurek
 */
public class BotControlThread implements Runnable {

    private final Scanner inputReader;
    private final CommandHandler commandHandler;
    private final static String quitCommand = "/q";
    
    /**
     * Creates a new Object of the BotControlThread
     * @param commandHandler the command handler which will handle commands of the user.
     */
    public BotControlThread(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        this.inputReader = StaticScanner.scanner;
    }
    
    @Override
    public void run() {
        String input;
        System.out.println("Input command:");
        while (true) {
            if (inputReader.hasNext()) {
                input = inputReader.nextLine();
                this.commandHandler.handleCommand(input);
                
                if(input.equalsIgnoreCase(quitCommand))
                    return;
                System.out.println("Input command:");
            }
        }
    }

}
