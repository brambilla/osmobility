package it.unipr.ce.dsg.osmobility.mobility.node;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Resource;
import it.unipr.ce.dsg.osmobility.communication.Message;
import it.unipr.ce.dsg.osmobility.communication.event.SendMessageEvent;
import it.unipr.ce.dsg.osmobility.communication.model.CommunicationModel;
import it.unipr.ce.dsg.osmobility.util.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Properties;

import com.google.gson.JsonObject;

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
	 * Retrieves all the {@link GeoNode}s whose {@link Location} is less than radius from the specified location.
	 * @param location
	 * - the location around which to retrieve the {@link GeoNode}s
	 * @param radius
	 * - maximum distance of {@link GeoNode}s from the location (in km)
	 * @return
	 * all the {@link GeoNode}s whose {@link Location} is less than radius from the specified location
	 */
	public HashSet<GeoNode> getGeoNodes(Location location, Double radius) {
		HashSet<GeoNode> geoNodes = new HashSet<GeoNode>();
		for(Node node : Engine.getDefault().getNodes()) {
			if(node instanceof GeoNode) {
				GeoNode geoNode = (GeoNode) node;
				if(geoNode.getLocation().distanceFrom(location) <= radius) {
					geoNodes.add(geoNode);
				}
			}
		}
		return geoNodes;
	}

	/**
	 * Retrieves all the {@link GeoNode}s with the specified id whose {@link Location} is less than radius from the specified location.
	 * @param location
	 * - the location around which to retrieve the {@link GeoNode}s
	 * @param radius
	 * - maximum distance of {@link GeoNode}s from the location (in km)
	 * @param id
	 * - the id of the {@link GeoNode}s
	 * @return
	 * all the {@link GeoNode}s with the specified id whose {@link Location} is less than radius from the specified location
	 */
	public HashSet<GeoNode> getGeoNodesById(Location location, Double radius, String id) {
		HashSet<GeoNode> geoNodes = new HashSet<GeoNode>();
		for(Node node : Engine.getDefault().getNodes()) {
			if(node instanceof GeoNode) {
				GeoNode geoNode = (GeoNode) node;
				if(geoNode.getId().equalsIgnoreCase(id) && geoNode.getLocation().distanceFrom(location) <= radius) {
					geoNodes.add(geoNode);
				}
			}
		}
		return geoNodes;
	}

	/**
	 * Retrieves the nearest {@link GeoNode} to the specified location.
	 * @param location
	 * - the location around which to retrieve the nearest {@link GeoNode}
	 * @return
	 * the nearest {@link GeoNode} to the specified location
	 */
	public GeoNode getNearestGeoNode(Location location) {
		GeoNode nearestGeoNode = null;
		for(Node node : Engine.getDefault().getNodes()) {
			if(node instanceof GeoNode) {
				GeoNode geoNode = (GeoNode) node;
				if(nearestGeoNode == null || (geoNode.getLocation().distanceFrom(location) < nearestGeoNode.getLocation().distanceFrom(location))) {
					nearestGeoNode = geoNode;
				}
			}
		}
		return nearestGeoNode;
	}

	/**
	 * Retrieves the nearest {@link GeoNode} to the specified location with the specified id.
	 * @param location
	 * - the location around which to retrieve the nearest {@link GeoNode}
	 * @param id
	 * - the id of the nearest {@link GeoNode}
	 * @return
	 * the nearest {@link GeoNode} to the specified location with the specified id
	 */
	public GeoNode getNearestGeoNode(Location location, String id) {
		GeoNode nearestGeoNode = null;
		for(Node node : Engine.getDefault().getNodes()) {
			if(node instanceof GeoNode) {
				GeoNode geoNode = (GeoNode) node;
				if(geoNode.getId().equalsIgnoreCase(id)) {
					if(nearestGeoNode == null || (geoNode.getLocation().distanceFrom(location) < nearestGeoNode.getLocation().distanceFrom(location))) {
						nearestGeoNode = geoNode;
					}
				}
			}
		}
		return nearestGeoNode;
	}

	/**
	 * Returns all the {@link GeoNode}s sorted by their distance from the specified location.
	 * @param location
	 * - the specified location
	 * @return
	 * all the {@link GeoNode}s sorted by their distance from the specified location
	 */
	public ArrayList<GeoNode> getSortedGeoNodes(final Location location) {
		ArrayList<GeoNode> sortedGeoNodes = new ArrayList<GeoNode>();
		for(Node node : Engine.getDefault().getNodes()) {
			if(node instanceof GeoNode) {
				GeoNode geoNode = (GeoNode) node;
				sortedGeoNodes.add(geoNode);
			}
		}
		Collections.sort(sortedGeoNodes, new Comparator<GeoNode>() {

			@Override
			public int compare(GeoNode geoNode0, GeoNode geoNode1) {
				return geoNode0.getLocation().distanceFrom(location).compareTo(geoNode1.getLocation().distanceFrom(location));
			}

		});

		return sortedGeoNodes;
	}

	/**
	 * Returns all the {@link GeoNode}s with the specified id, sorted by their distance from the specified location.
	 * @param location
	 * - the specified location
	 * @param id
	 * - the specified id
	 * @return
	 * all the {@link GeoNode}s sorted by their distance from the specified location
	 */
	public ArrayList<GeoNode> getSortedGeoNodes(final Location location, String id) {
		ArrayList<GeoNode> sortedGeoNodes = new ArrayList<GeoNode>();
		for(Node node : Engine.getDefault().getNodes()) {
			if(node instanceof GeoNode) {
				GeoNode geoNode = (GeoNode) node;
				if(geoNode.getId().equalsIgnoreCase(id)) {
					sortedGeoNodes.add(geoNode);
				}
			}
		}
		Collections.sort(sortedGeoNodes, new Comparator<GeoNode>() {

			@Override
			public int compare(GeoNode geoNode0, GeoNode geoNode1) {
				return geoNode0.getLocation().distanceFrom(location).compareTo(geoNode1.getLocation().distanceFrom(location));
			}

		});

		return sortedGeoNodes;
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
