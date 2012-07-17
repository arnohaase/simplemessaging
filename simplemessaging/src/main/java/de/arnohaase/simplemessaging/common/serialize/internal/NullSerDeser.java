package de.arnohaase.simplemessaging.common.serialize.internal;

import java.io.InputStream;
import java.io.OutputStream;


public class NullSerDeser implements SerDeser {
    public boolean canHandle (Object o) {
        return o == null;
    }

    public void serialize (OutputStream out, Object o, ISerializer s) {
    }

    public Object deserialize (InputStream in, IDeserializer d) {
        return null;
    }
}
