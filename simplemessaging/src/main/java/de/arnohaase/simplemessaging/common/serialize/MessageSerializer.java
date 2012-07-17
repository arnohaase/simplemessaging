package de.arnohaase.simplemessaging.common.serialize;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import de.arnohaase.simplemessaging.common.serialize.internal.BooleanSerDeser;
import de.arnohaase.simplemessaging.common.serialize.internal.IDeserializer;
import de.arnohaase.simplemessaging.common.serialize.internal.ISerializer;
import de.arnohaase.simplemessaging.common.serialize.internal.IntSerDeser;
import de.arnohaase.simplemessaging.common.serialize.internal.ListSerDeser;
import de.arnohaase.simplemessaging.common.serialize.internal.LongSerDeser;
import de.arnohaase.simplemessaging.common.serialize.internal.MapSerDeser;
import de.arnohaase.simplemessaging.common.serialize.internal.MessageSerDeser;
import de.arnohaase.simplemessaging.common.serialize.internal.NullSerDeser;
import de.arnohaase.simplemessaging.common.serialize.internal.SerDeser;
import de.arnohaase.simplemessaging.common.serialize.internal.SerDeserHelper;
import de.arnohaase.simplemessaging.common.serialize.internal.SetSerDeser;
import de.arnohaase.simplemessaging.common.serialize.internal.StringSerDeser;


public class MessageSerializer implements ISerializer, IDeserializer {
    private static final List<SerDeser> _serializers = new ArrayList<SerDeser> (); 

    static {
        _serializers.add (new NullSerDeser ());
        _serializers.add (new BooleanSerDeser ());
        _serializers.add (new IntSerDeser ());
        _serializers.add (new LongSerDeser ());
        _serializers.add (new StringSerDeser ());
        _serializers.add (new ListSerDeser ());
        _serializers.add (new SetSerDeser ());
        _serializers.add (new MapSerDeser ());
        _serializers.add (new MessageSerDeser ());
    }

    public static void register (SerDeser primitiveSerializer) {
        _serializers.add (0, primitiveSerializer);
    }
    
    public void serialize (OutputStream out, Object o) throws Exception {
        final SerDeser s = findSerializer (o);
        new SerDeserHelper ().writeString (out, s.getClass ().getName ());
        s.serialize (out, o, this);
    }

    public Object deserialize (InputStream in) throws Exception {
        final String deserClassname = new SerDeserHelper ().readString (in);
        final SerDeser deser = (SerDeser) Class.forName (deserClassname).newInstance ();
        return deser.deserialize (in, this);
    }

    private SerDeser findSerializer (Object o) {
        for (SerDeser s: _serializers)
            if (s.canHandle (o))
                return s;
        
        throw new IllegalArgumentException ("no serializer for " + o + " of type " + o.getClass ().getName ());
    }
}
