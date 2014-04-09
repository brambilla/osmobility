package it.unipr.ce.dsg.osmobility.mobility.node;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Resource;
import it.unipr.ce.dsg.osmobility.mobility.event.MoveNodeEvent;
import it.unipr.ce.dsg.osmobility.mobility.model.MobilityModel;
import it.unipr.ce.dsg.osmobility.util.Location;
import it.unipr.ce.dsg.osmobility.util.Path;

import java.util.ArrayList;
import java.util.Properties;

import com.google.gson.JsonObject;

/**
 * 
 * @author Giacomo Brambilla (giacomo.brambilla@studenti.unipr.it)
 *
 */

public abstract class MobileNode extends GeoNode {

	private Path path;
	private Location location;
	private Location nextLocation;
	private MobilityModel mobilityModel;

	public MobileNode(String id, Properties params, ArrayList<Resource> resources) throws InvalidParamsException {
		super(id, params, resources);
	}

	@Override
	public Location getLocation() {
		return location;
	}

	/**
	 * Schedules next move of the node considering the mobility model.
	 */
	public void scheduleNextMove() {
		if(path.hasNextLocation(location)) {
			nextLocation = path.nextLocation(location);

			Double millis = mobilityModel.evaluateMove(getKey(), location, nextLocation, path);
			Float delay = fromMillisToVirtualTime(millis);
			
			if(delay > 0) {
				MoveNodeEvent moveNodeEvent;
				try {
					moveNodeEvent = (MoveNodeEvent) new MoveNodeEvent("MoveNodeEvent", params, null).createInstance(Engine.getDefault().getVirtualTime() + delay);
					moveNodeEvent.setOneShot(true);
					moveNodeEvent.setAssociatedNode(this);
					Engine.getDefault().insertIntoEventsList(moveNodeEvent);
				} catch (InvalidParamsException e) {
					e.printStackTrace();
				}
			}
		} else {
			onPathEnd();
		}
	}

	/**
	 * Moves the node to next location.
	 */
	public void move() {
		location = nextLocation;
		onLocationChanged(new Location(location));
		scheduleNextMove();
	}

	/**
	 * @return path
	 */
	public Path getPath() {
		return path;
	}

	/**
	 * Initialize mobility of the node with a path and a mobility model.
	 * @param path
	 * - the path travelled by the node.
	 * @param mobilityModel
	 * - the mobility model adopted by the node.
	 */
	public void initMobility(Path path, MobilityModel mobilityModel) {
		this.path = path;
		this.mobilityModel = mobilityModel;
		this.location = path.getFirstLocation();
	}

	@Override
	public JsonObject toJsonObject() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("key", getKey());
		jsonObject.add("location", location.toJsonObject());
		return jsonObject;
	}

	/**
	 * @return mobilityModel
	 */
	public MobilityModel getMobilityModel() {
		return mobilityModel;
	}

	/**
	 * Called when the location has changed.
	 * There are no restrictions on the use of the supplied Location object.
	 * 
	 * @param location
	 * - The new location, as a Location object.
	 */
	public abstract void onLocationChanged(Location location);

	/**
	 * Called when the path has ended.
	 * 
	 */
	public abstract void onPathEnd();

}
