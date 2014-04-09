package it.unipr.ce.dsg.osmobility.mobility.node;

import java.util.ArrayList;
import java.util.Properties;

import com.google.gson.JsonObject;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Resource;
import it.unipr.ce.dsg.osmobility.communication.Message;
import it.unipr.ce.dsg.osmobility.communication.event.SendMessageEvent;
import it.unipr.ce.dsg.osmobility.communication.model.CommunicationModel;
import it.unipr.ce.dsg.osmobility.util.Location;

/**
 * 
 * @author Giacomo Brambilla (giacomo.brambilla@studenti.unipr.it)
 *
 */

public abstract class GeoNode extends Node {
	
	private CommunicationModel communicationModel;

	public GeoNode(String id, Properties params, ArrayList<Resource> resources) throws InvalidParamsException {
		super(id, params, resources);
	}
	
	/**
	 * Send a message to a node considering the communication model.
	 * @param message
	 * - message to send.
	 * @param geoNode
	 * - node recipient of the message.
	 */
	public void send(Message message, GeoNode geoNode) {
		Double millis = communicationModel.evaluateSend(message, geoNode);
		Float delay = fromMillisToVirtualTime(millis);
		if(delay > 0) {
			SendMessageEvent sendMessageEvent;
			try {
				sendMessageEvent = (SendMessageEvent) new SendMessageEvent("SendMessageEvent", params, null).createInstance(Engine.getDefault().getVirtualTime() + delay);
				sendMessageEvent.setOneShot(true);
				sendMessageEvent.setMessage(message);
				sendMessageEvent.setSenderNode(this);
				sendMessageEvent.setAssociatedNode(geoNode);
				Engine.getDefault().insertIntoEventsList(sendMessageEvent);
			} catch (InvalidParamsException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Initialize communication of the node with a communication model.
	 * @param communicationModel
	 * - the communication model adopted by the node.
	 */
	public void initCommunication(CommunicationModel communicationModel) {
		this.communicationModel = communicationModel;
	}

	/**
	 * Converts from virtual time to millis.
	 * @param virtualTime
	 * - virtual time you want convert to millis.
	 * @return
	 * the value of millis converted from virtual time.
	 */
	public abstract Double fromVirtualTimetoMillis(Float virtualTime);

	/**
	 * Converts from millis to virtual time.
	 * @param millis
	 * - millis you want convert to virtual time.
	 * @return
	 * the value of virtual time converted from millis.
	 */
	public abstract Float fromMillisToVirtualTime(Double millis);

	/**
	 * Returns a JsonObject that represents the GeoNode.
	 * @return
	 * a JsonObject that represents the GeoNode.
	 */
	public abstract JsonObject toJsonObject();
	
	/**
	 * Called when a message has received.
	 * @param message
	 * - message received.
	 * @param senderNode
	 * - sender of the message.
	 */
	public abstract void onReceivedMessage(Message message, GeoNode senderNode);
	
	/**
	 * Returns actual location of the node.
	 * @return
	 * actual location of the node.
	 */
	public abstract Location getLocation();

}
