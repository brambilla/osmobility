package it.unipr.ce.dsg.osmobility.mobility.event;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.osmobility.mobility.node.StationaryNode;
import it.unipr.ce.dsg.osmobility.util.Location;

import java.util.Properties;

/**
 * 
 * @author Giacomo Brambilla (giacomo.brambilla@studenti.unipr.it)
 *
 */

public abstract class StationaryNodeBirthEvent extends GeoNodeBirthEvent {

	private Location location;

	public StationaryNodeBirthEvent(String id, Properties params, Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);

		String latitudeParam = params.getProperty("latitude");
		Double latitude;
		try {
			latitude = Double.valueOf(latitudeParam);
		} catch(NumberFormatException e) {
			throw new InvalidParamsException("Error in latitude param");
		}

		String longitudeParam = params.getProperty("longitude");
		Double longitude;
		try {
			longitude = Double.valueOf(longitudeParam);
		} catch(NumberFormatException e) {
			throw new InvalidParamsException("Error in longitude param");
		}

		location = new Location(latitude, longitude);
	}

	@Override
	public void run() throws RunException {
		super.run();
		StationaryNode stationaryNode = (StationaryNode) associatedNode;
		stationaryNode.setLocation(location);
	}

}
