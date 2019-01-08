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

import de.dkwr.bompp.cmd.handler.CommandHandler;
import java.util.Optional;

/**
 *
 * @author Dennis Kawurek
 */
public class ExecCommand extends AbstractBotCommand {

    private final CommandHandler scriptCommandHandler;

    public ExecCommand(CommandHandler scriptCommandHandler) {
        super("/exec", Optional.empty(), "Execute a command from Bot CLI", 1);
        this.scriptCommandHandler = scriptCommandHandler;
    }

    @Override
    public boolean exec(String cmd, Optional<String> params) throws IllegalArgumentException {
        //String execCmd = cmd.substring(cmdArr[0].length() + 1);
        if (!this.paramsNumberValid(params)) {
            throw new IllegalArgumentException("To execute a command you need to call " + this.usage());
        }

        String execCmd = params.get();
        System.out.println(execCmd);
        this.scriptCommandHandler.handleCommand(execCmd);
        return true;
    }

}
