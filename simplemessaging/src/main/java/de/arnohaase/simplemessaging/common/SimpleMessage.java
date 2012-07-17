package de.arnohaase.simplemessaging.common;


/**
 * This is a possible implementation of the Message interface, taking all data in its constructor.
 * 
 * @author arno
 */
public class SimpleMessage implements Message {
    private final long _seqNumber;
    private final MessageCategory _category;
    private final String _categoryDetails;
    private final Object _data;

    
    public SimpleMessage (long seqNumber, MessageCategory category, String categoryDetails, Object data) {
        _seqNumber = seqNumber;
        _category = category;
        _categoryDetails = categoryDetails;
        _data = data;
    }

    public long getSeqNumber () {
        return _seqNumber;
    }
    
    public MessageCategory getCategory () {
        return _category;
    }

    public String getCategoryDetails () {
        return _categoryDetails;
    }

    public Object getData () {
        return _data;
    }

    public String toString () {
        return "SimpleMessage [_category=" + _category + ", _categoryDetails=" + _categoryDetails + ", _data=" + _data + ", _seqNumber=" + _seqNumber + "]";
    }
}
