package de.arnohaase.simplemessaging.common.serialize.internal;

import java.io.InputStream;


public interface IDeserializer {
    Object deserialize (InputStream in) throws Exception;
}
