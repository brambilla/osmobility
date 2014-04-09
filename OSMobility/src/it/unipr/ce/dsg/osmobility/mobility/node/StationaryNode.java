package it.unipr.ce.dsg.osmobility.mobility.node;

import java.util.ArrayList;
import java.util.Properties;

import com.google.gson.JsonObject;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Resource;
import it.unipr.ce.dsg.osmobility.util.Location;

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
	public void initialize() throws InvalidParamsException {
		try {
		Double latitude = Double.parseDouble(params.getProperty("latitude"));
		Double longitude = Double.parseDouble(params.getProperty("longitude"));
		setLocation(new Location(latitude, longitude));
		} catch(NullPointerException e) {
			throw new InvalidParamsException("null params exception");
		} catch(NumberFormatException e) {
			throw new InvalidParamsException("number format params exception");
		}
		
	}
	
	@Override
	public JsonObject toJsonObject() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("key", getKey());
		jsonObject.add("location", location.toJsonObject());
		return jsonObject;
	}
}
