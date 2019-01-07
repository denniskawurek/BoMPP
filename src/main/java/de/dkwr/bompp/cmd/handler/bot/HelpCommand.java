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

import java.util.HashMap;
import java.util.Optional;

/**
 *
 * @author Dennis Kawurek
 */
public class HelpCommand extends AbstractBotCommand {

    private final HashMap<String, AbstractBotCommand> cmdMap;
    
    public HelpCommand(HashMap<String, AbstractBotCommand> cmdMap) {
        super("/help", Optional.empty(), "Prints the help");
        this.cmdMap = cmdMap;
    }

    @Override
    public boolean exec(String cmd, Optional<String> params) {
        StringBuilder cmdStr = new StringBuilder();
        
        cmdMap.forEach((k, v) -> {
            cmdStr.append(v.usage());
        });
        System.out.println(cmdStr);
        return true;
    }
    
}
