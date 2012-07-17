package de.arnohaase.simplemessaging.common.serialize.internal;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;


public class SetSerDeser implements SerDeser {
    public boolean canHandle (Object o) {
        return o instanceof Set<?>;
    }

    public void serialize (OutputStream out, Object o, ISerializer forRecursiveDescent) throws Exception {
        final Set<?> l = (Set<?>) o;
        new SerDeserHelper ().writeFourBytes (out, l.size ());
        for (Object element: l)
            forRecursiveDescent.serialize (out, element);
    }

    public Object deserialize (InputStream in, IDeserializer forRecursiveDescent) throws Exception {
        final Set<Object> result = new HashSet<Object> ();
        final int size = new SerDeserHelper ().readFourBytes (in);
        for (int i=0; i<size; i++)
            result.add (forRecursiveDescent.deserialize (in));
        return result;
    }
}
