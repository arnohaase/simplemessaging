package de.arnohaase.simplemessaging.common.serialize.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class LongSerDeser implements SerDeser {
    public boolean canHandle (Object o) {
        return o instanceof Long;
    }

    public void serialize (OutputStream out, Object o, ISerializer s) throws IOException {
        new SerDeserHelper ().writeEightBytes (out,  (Long) o);
    }

    public Object deserialize (InputStream in, IDeserializer d) throws IOException {
        return new SerDeserHelper ().readEightBytes (in);
    }
}
