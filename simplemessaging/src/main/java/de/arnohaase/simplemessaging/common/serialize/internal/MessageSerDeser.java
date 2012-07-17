package de.arnohaase.simplemessaging.common.serialize.internal;

import java.io.InputStream;
import java.io.OutputStream;

import de.arnohaase.simplemessaging.common.Message;
import de.arnohaase.simplemessaging.common.MessageCategory;
import de.arnohaase.simplemessaging.common.SimpleMessage;


public class MessageSerDeser implements SerDeser {
    @Override
    public boolean canHandle (Object o) {
        return o instanceof Message;
    }

    @Override
    public void serialize (OutputStream out, Object o, ISerializer forRecursiveDescent) throws Exception {
        final Message m = (Message) o;
        forRecursiveDescent.serialize (out, m.getSeqNumber ());
        forRecursiveDescent.serialize (out, m.getCategory ().getName ());
        forRecursiveDescent.serialize (out, m.getCategoryDetails ());
        forRecursiveDescent.serialize (out, m.getData ());
    }

    @Override
    public Object deserialize (InputStream in, IDeserializer forRecursiveDescent) throws Exception {
        final long seqNumber = (Long) forRecursiveDescent.deserialize (in);
        final MessageCategory category = new MessageCategory ((String) forRecursiveDescent.deserialize (in));
        final String categoryDetails = (String) forRecursiveDescent.deserialize (in);
        final Object data = forRecursiveDescent.deserialize (in);

        return new SimpleMessage (seqNumber, category, categoryDetails, data);
    }
}
