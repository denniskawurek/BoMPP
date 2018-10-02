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

import de.dkwr.bompp.util.BotConfiguration;
import de.dkwr.bompp.util.BotLogger;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import org.jivesoftware.smackx.omemo.internal.OmemoDevice;
import org.jivesoftware.smackx.omemo.trust.OmemoFingerprint;
import org.jivesoftware.smackx.omemo.trust.OmemoTrustCallback;
import org.jivesoftware.smackx.omemo.trust.TrustState;

/**
 * Implements the OmemoTrustCallback for storing trusted/untrusted fingerprints.
 * @author Dennis Kawurek
 */
public class BotTrustCallback implements OmemoTrustCallback {
    
    private static BotTrustCallback instance;
    private HashMap<Integer, HashMap<String, TrustState>> trustStates;
    private static final String SERIALIZE_FILE = BotConfiguration.getInstance().getTrustedStatesFilePath();
    
    public BotTrustCallback() {
        this.trustStates = deserialize();
        instance = this;
    }

    @Override
    public TrustState getTrust(OmemoDevice device, OmemoFingerprint fingerprint) {
        HashMap<String, TrustState> states = trustStates.get(device.getDeviceId());
        if (states != null) {
            TrustState state = states.get(fingerprint.blocksOf8Chars());
            if (state != null) {
                return state;
            }
        }
        return TrustState.undecided;
    }

    @Override
    public void setTrust(OmemoDevice device, OmemoFingerprint fingerprint, TrustState truststate) {
        HashMap<String, TrustState> states = trustStates.get(device.getDeviceId());
        if (states == null) {
            states = new HashMap<>();
            trustStates.put(device.getDeviceId(), states);
        }
        states.put(fingerprint.blocksOf8Chars(), truststate);
        serialize();
    }

    public static BotTrustCallback getInstance() {
        return instance;
    }
    
    private void serialize() {
        try {
            FileOutputStream fileOut = new FileOutputStream(SERIALIZE_FILE);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this.trustStates);
            out.close();
            fileOut.close();
        } catch (FileNotFoundException ex) {
            BotLogger.getInstance().logException(ex);
        } catch (IOException ex) {
            BotLogger.getInstance().logException(ex);
        }
    }
    
    private static HashMap deserialize() {
        try {
            FileInputStream fileIn = new FileInputStream(SERIALIZE_FILE);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            Object readObj = in.readObject();
            if(readObj == null)
                return new HashMap<>();
            else
                return (HashMap) readObj;
            
        } catch (FileNotFoundException f) {
            return new HashMap<>();
        } catch (ClassNotFoundException c) {
            BotLogger.getInstance().logMsg("Class not found in method deserialize() in BotTrustCallback.");
            BotLogger.getInstance().logException(c);
        } catch (IOException ex) {
            BotLogger.getInstance().logException(ex);
        }
        return new HashMap<>();
    }
}
