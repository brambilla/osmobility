package it.unipr.ce.dsg.osmobility.communication;

/**
 * 
 * @author Giacomo Brambilla (giacomo.brambilla@studenti.unipr.it)
 *
 */

public interface Message {
	
	/**
	 * Returns the type of the message.
	 * @return
	 * a String that represents the type of the message.
	 */
	public String getType();
	
	/**
	 * Returns the payload of the message.
	 * @return
	 * the payload 
	 */
	public byte[] getPayload();
	
	/**
	 * Returns the number of bytes of the message.
	 * @return
	 * - the number of bytes of the message.
	 */
	public Integer getBytesLength();
}
