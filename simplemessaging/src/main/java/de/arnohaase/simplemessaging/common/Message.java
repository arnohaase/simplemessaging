package de.arnohaase.simplemessaging.common;



/**
 * Implementations must be thread safe, ideally being immutable. This is alos true for the payload returned by getData().
 * 
 * @author arno
 */
public interface Message {
    long getSeqNumber ();
    
    /**
     * category and (optional) categoryDetails describe what the message is about. These properties together are the basis
     *  for the decision whether a message is relevant for a given consumer.<br>
     *  
     * The data types are intentionally generic, allowing applications to customize, e.g.:<br>
     * 
     * <ul>
     * <li> categorey="MachineMalfunction", categoryDetails=<id of the failing machine>
     * <li> category="VehicleMoved", categoryDetails=<id of the vehicle's new position>
     * </ul>
     */
    MessageCategory getCategory ();
    String getCategoryDetails (); 
    
    /**
     * This is the actual payload of the message
     */
    Object getData () throws Exception; 
}
