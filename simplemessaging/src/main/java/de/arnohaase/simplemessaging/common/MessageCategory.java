package de.arnohaase.simplemessaging.common;

import java.io.Serializable;


public final class MessageCategory implements Serializable {
    private final String _name;

    public MessageCategory (String name) {
        _name = name;
    }

    public String getName () {
        return _name;
    }

    @Override
    public String toString () {
        return "MessageCategory [_name=" + _name + "]";
    }

    @Override
    public int hashCode () {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_name == null) ? 0 : _name.hashCode ());
        return result;
    }

    @Override
    public boolean equals (Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass () != obj.getClass ())
            return false;
        MessageCategory other = (MessageCategory) obj;
        if (_name == null) {
            if (other._name != null)
                return false;
        } else if (!_name.equals (other._name))
            return false;
        return true;
    }
}
