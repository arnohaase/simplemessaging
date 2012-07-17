package de.arnohaase.simplemessaging.common.serialize.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class StringSerDeser implements SerDeser {
    public boolean canHandle (Object o) {
        return o instanceof String;
    }

    public void serialize (OutputStream out, Object o, ISerializer s) throws IOException {
        new SerDeserHelper ().writeString (out, (String) o);
    }

    public Object deserialize (InputStream in, IDeserializer d) throws IOException {
        return new SerDeserHelper ().readString (in);
    }
}
