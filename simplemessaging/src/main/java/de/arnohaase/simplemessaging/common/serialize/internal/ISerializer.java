package de.arnohaase.simplemessaging.common.serialize.internal;

import java.io.OutputStream;


public interface ISerializer {
    void serialize (OutputStream out, Object o) throws Exception;
}
