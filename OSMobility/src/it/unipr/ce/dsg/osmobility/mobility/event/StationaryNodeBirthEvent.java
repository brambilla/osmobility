package it.unipr.ce.dsg.osmobility.mobility.event;

import it.unipr.ce.dsg.deus.core.Engine;
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

	Double latitude = null;
	Double longitude = null;

	Double minimumLongitude = null;
	Double maximumLongitude = null;
	Double maximumLatitude = null;
	Double minimumLatitude = null;


	public StationaryNodeBirthEvent(String id, Properties params, Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);

		if(params.containsKey("latitude") || params.containsKey("longitude")) {
			String latitudeParam = params.getProperty("latitude");
			try {
				latitude = Double.valueOf(latitudeParam);
			} catch(NumberFormatException e) {
				throw new InvalidParamsException("Error in latitude param");
			}

			String longitudeParam = params.getProperty("longitude");
			try {
				longitude = Double.valueOf(longitudeParam);
			} catch(NumberFormatException e) {
				throw new InvalidParamsException("Error in longitude param");
			}
		} else {

			String minimumLongitudeParam = params.getProperty("minimumLongitude");
			try {
				minimumLongitude = Double.valueOf(minimumLongitudeParam);
			} catch(NumberFormatException e) {
				throw new InvalidParamsException("Error in minimum longitude param");
			}

			String maximumLongitudeParam = params.getProperty("maximumLongitude");
			try {
				maximumLongitude = Double.valueOf(maximumLongitudeParam);
			} catch(NumberFormatException e) {
				throw new InvalidParamsException("Error in maximum longitude param");
			}

			String maximumLatitudeParam = params.getProperty("maximumLatitude");
			try {
				maximumLatitude = Double.valueOf(maximumLatitudeParam);
			} catch(NumberFormatException e) {
				throw new InvalidParamsException("Error in maximum latitude param");
			}

			String minimumLatitudeParam = params.getProperty("minimumLatitude");
			try {
				minimumLatitude = Double.valueOf(minimumLatitudeParam);
			} catch(NumberFormatException e) {
				throw new InvalidParamsException("Error in minimum latitude param");
			}

		}
	}

	@Override
	public void run() throws RunException {
		super.run();

		try {
			if(latitude == null || longitude == null) {
				latitude = minimumLatitude + Engine.getDefault().getSimulationRandom().nextDouble() * (maximumLatitude - minimumLatitude);
				longitude = minimumLongitude + Engine.getDefault().getSimulationRandom().nextDouble() * (maximumLongitude - minimumLongitude);
			}
		} catch(NullPointerException e) {
			throw new RunException("Error un params");
		}

		StationaryNode stationaryNode = (StationaryNode) associatedNode;
		stationaryNode.setLocation(new Location(latitude, longitude));
	}

}
