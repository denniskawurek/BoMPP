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
import java.util.HashMap;
import org.jivesoftware.smackx.omemo.internal.OmemoDevice;
import org.jivesoftware.smackx.omemo.trust.OmemoFingerprint;
import org.jivesoftware.smackx.omemo.trust.OmemoTrustCallback;
import org.jivesoftware.smackx.omemo.trust.TrustState;

/**
 *
 * @author Dennis Kawurek
 */
public class BotTrustCallback implements OmemoTrustCallback {
    HashMap<OmemoDevice, HashMap<String, TrustState>> trustMap = new HashMap<>();
    private static final BotTrustCallback instance = new BotTrustCallback();
     private final HashMap<OmemoDevice, HashMap<OmemoFingerprint, TrustState>> trustStates = new HashMap<>();
    
    @Override
    public TrustState getTrust(OmemoDevice od, OmemoFingerprint of) {
        HashMap<OmemoFingerprint, TrustState> states = trustStates.get(od);

        if (states != null) {
            TrustState state = states.get(of);

            if (state != null) {
                return state;
            }
        }

return TrustState.undecided;
    }

    @Override
    public void setTrust(OmemoDevice od, OmemoFingerprint of, TrustState ts) {
        HashMap<OmemoFingerprint, TrustState> states = trustStates.get(od);

        if (states == null) {
            states = new HashMap<>();
            trustStates.put(od, states);
        }

states.put(of, ts);
    }
    
    public static BotTrustCallback getInstance() {
        return instance;
    }
    
}
