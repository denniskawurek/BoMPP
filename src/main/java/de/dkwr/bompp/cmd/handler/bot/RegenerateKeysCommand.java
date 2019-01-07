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

import de.dkwr.bompp.util.BotLogger;
import de.dkwr.bompp.xmpp.OmemoController;
import java.util.Optional;

/**
 *
 * @author Dennis Kawurek
 */
public class RegenerateKeysCommand extends AbstractBotCommand {

    private final OmemoController omemoController;

    public RegenerateKeysCommand(OmemoController omemoController) {
        super("/regenerate", Optional.empty(), "Regenerate keys");
        this.omemoController = omemoController;
    }

    @Override
    public boolean exec(String cmd, Optional<String> params) throws IllegalArgumentException {
        try {
            this.omemoController.regenerateKeys();
        } catch (Exception ex) {
            BotLogger.getInstance().logException(ex);
            return false;
        }
        return true;
    }

}
