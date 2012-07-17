package de.arnohaase.simplemessaging.common.util;


public class Pair <T1, T2> {
    private final T1 _first;
    private final T2 _second;
    
    public Pair (T1 first, T2 second) {
        _first = first;
        _second = second;
    }

    public T1 getFirst () {
        return _first;
    }

    public T2 getSecond () {
        return _second;
    }

    
    @Override
    public String toString () {
        return "Pair [_first=" + _first + ", _second=" + _second + "]";
    }

    @Override
    public int hashCode () {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_first == null) ? 0 : _first.hashCode ());
        result = prime * result + ((_second == null) ? 0 : _second.hashCode ());
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
        Pair<?,?> other = (Pair<?,?>) obj;
        if (_first == null) {
            if (other._first != null)
                return false;
        } else if (!_first.equals (other._first))
            return false;
        if (_second == null) {
            if (other._second != null)
                return false;
        } else if (!_second.equals (other._second))
            return false;
        return true;
    }
}
