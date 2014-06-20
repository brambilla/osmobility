package it.unipr.ce.dsg.osmobility.mobility.node;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Resource;
import it.unipr.ce.dsg.osmobility.util.Location;

import java.util.ArrayList;
import java.util.Properties;

import com.google.gson.JsonObject;

/**
 * 
 * @author Giacomo Brambilla (giacomo.brambilla@studenti.unipr.it)
 * 
 */

public abstract class StationaryNode extends GeoNode {

	private Location location;

	public StationaryNode(String id, Properties params, ArrayList<Resource> resources)	throws InvalidParamsException {
		super(id, params, resources);
	}

	@Override
	public Location getLocation() {
		return location;
	}

	/**
	 * @param location
	 * - location to set.
	 */
	public void setLocation(final Location location) {
		this.location = location;
	}

	@Override
	public JsonObject toJsonObject() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("key", getKey());
		jsonObject.add("location", location.toJsonObject());
		return jsonObject;
	}
}
