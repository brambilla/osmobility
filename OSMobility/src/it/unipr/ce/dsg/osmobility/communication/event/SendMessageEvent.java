package it.unipr.ce.dsg.osmobility.communication.event;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.osmobility.communication.Message;
import it.unipr.ce.dsg.osmobility.mobility.node.GeoNode;

/**
 * 
 * @author Giacomo Brambilla (giacomo.brambilla@studenti.unipr.it)
 *
 */

public class SendMessageEvent extends Event {
	
	private GeoNode senderNode;
	private Message message;

	public SendMessageEvent(String id, Properties params, Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
	}

	@Override
	public void run() throws RunException {
		GeoNode receiverNode = (GeoNode) getAssociatedNode();		
		receiverNode.onReceivedMessage(message, senderNode);
	}

	/**
	 * @param senderNode
	 * - senderNode to set.
	 */
	public void setSenderNode(GeoNode senderNode) {
		this.senderNode = senderNode;
	}

	/**
	 * @param message
	 * - message to set.
	 */
	public void setMessage(Message message) {
		this.message = message;
	}
}
