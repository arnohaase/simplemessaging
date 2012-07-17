package de.arnohaase.simplemessaging.server.messaging.impl.hibernate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import de.arnohaase.simplemessaging.common.Message;
import de.arnohaase.simplemessaging.common.MessageCategory;
import de.arnohaase.simplemessaging.common.serialize.MessageSerializer;


@Entity
@GenericGenerator (name="simple-messaging", strategy="sequence")
public class HibernateMessage implements Message {
    private long _msgNo;
    private Date _msgTimestamp;
    private String _category;
    private String _categoryDetails;
    private byte[] _msgData;
    
    
    public HibernateMessage () {
    }
    
    public HibernateMessage (Date msgTimestamp, String category, String categoryDetails, Object msgData) throws Exception {
        _msgTimestamp = msgTimestamp;
        _category = category;
        _categoryDetails = categoryDetails;
        setData (msgData);
    }


    @Id
    @GeneratedValue (generator="simple-messaging")
    public long getMsgNo () {
        return _msgNo;
    }
    
    public void setMsgNo (long msgNo) {
        _msgNo = msgNo;
    }
    
    @Temporal (TemporalType.TIMESTAMP)
    public Date getMsgTimestamp () {
        return _msgTimestamp;
    }
    
    public void setMsgTimestamp (Date msgTimestamp) {
        _msgTimestamp = msgTimestamp;
    }
    
    @Basic
    @Column (name="category")
    public String getCategoryRaw () {
        return _category;
    }
    
    public void setCategoryRaw (String category) {
        _category = category;
    }
    
    public String getCategoryDetails () {
        return _categoryDetails;
    }
    
    public void setCategoryDetails (String categoryDetails) {
        _categoryDetails = categoryDetails;
    }
    
    @Basic
    @Column (name="MsgData")
    public byte[] getMsgDataRaw () {
        return _msgData;
    }

    public void setMsgDataRaw (byte[] msgData) {
        _msgData = msgData;
    }
    
    @Transient
    public Object getData () throws Exception {
        if (getMsgDataRaw () == null)
            return null;
        
        return new MessageSerializer ().deserialize (new ByteArrayInputStream (getMsgDataRaw ()));
    }
    
    public void setData (Object data) throws Exception {
        if (data == null)
            setMsgDataRaw (null);
        else {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream ();
            new MessageSerializer ().serialize (baos, data);
            setMsgDataRaw (baos.toByteArray ());
        }
    }

    @Transient
    public long getSeqNumber () {
        return getMsgNo ();
    }
    
    @Transient
    public MessageCategory getCategory () {
        return new MessageCategory (getCategoryRaw ());
    }
}








