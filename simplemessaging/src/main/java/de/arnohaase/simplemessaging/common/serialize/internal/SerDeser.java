package de.arnohaase.simplemessaging.common.serialize.internal;

import java.io.InputStream;
import java.io.OutputStream;


public interface SerDeser {
    boolean canHandle (Object o);

    void serialize (OutputStream out, Object o, ISerializer forRecursiveDescent) throws Exception;
    Object deserialize (InputStream in, IDeserializer forRecursiveDescent) throws Exception; 
}
