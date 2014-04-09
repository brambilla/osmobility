package it.unipr.ce.dsg.osmobility.communication.model;

import it.unipr.ce.dsg.osmobility.communication.Message;
import it.unipr.ce.dsg.osmobility.mobility.node.GeoNode;

/**
 * 
 * @author Giacomo Brambilla (giacomo.brambilla@studenti.unipr.it)
 *
 */

public abstract class CommunicationModel {

	/**
	 * Evaluates the send considering actual message and GeoNode and returns the time necessary to the send.
	 * @param message
	 * - message to send.
	 * @param geoNode
	 * - geoNode to send the message.
	 * @return
	 * a Double representing the time necessary to the send (in millisecond).
	 */
	public abstract Double evaluateSend(Message message, GeoNode geoNode);

	/**
	 * Initializes the communication model with parameters specified in the configuration. The order of the parameters in the array is specified in XML file configuration.
	 * @param params
	 * - parameters specified in the configuration.
	 */
	public abstract void initialize(String[] params);

}
