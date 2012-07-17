package de.arnohaase.simplemessaging.common.serialize.internal;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;


public class MapSerDeser implements SerDeser {
    public boolean canHandle (Object o) {
        return o instanceof Map <?,?>;
    }

    public void serialize (OutputStream out, Object o, ISerializer forRecursiveDescent) throws Exception {
        final Map<?,?> m = (Map<?,?>) o;
        new SerDeserHelper ().writeFourBytes (out, m.size ());
        for (Map.Entry<?,?> entry: m.entrySet ()) {
            forRecursiveDescent.serialize (out, entry.getKey ());
            forRecursiveDescent.serialize (out, entry.getValue ());
        }
    }

    public Object deserialize (InputStream in, IDeserializer forRecursiveDescent) throws Exception {
        final Map <Object, Object> result = new HashMap <Object, Object> ();
        final int size = new SerDeserHelper ().readFourBytes (in);
        for (int i=0; i<size; i++) {
            final Object key   = forRecursiveDescent.deserialize (in);
            final Object value = forRecursiveDescent.deserialize (in);
            result.put (key, value);
        }
        return result;
    }
}
