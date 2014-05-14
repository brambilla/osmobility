package it.unipr.ce.dsg.osmobility.mobility.model;

import java.util.NoSuchElementException;

import it.unipr.ce.dsg.osmobility.exception.DirectionException;
import it.unipr.ce.dsg.osmobility.util.Location;
import it.unipr.ce.dsg.osmobility.util.Path;

/**
 * 
 * @author Giacomo Brambilla (giacomo.brambilla@studenti.unipr.it)
 *
 */

public class MinimumSpeedModel extends MobilityModel {

	@Override
	public Double evaluateMove(Integer key, Location location, Location nextLocation, Path path) {
		try {
			Double distance = location.distanceFrom(nextLocation);
			Double maximumSpeed = path.getOSMWay(location, nextLocation).getMinimumSpeed(path.getOSMDirection(location, nextLocation));
			return distance/maximumSpeed*60.0d*60.0d*1000.0d;
		} catch (NoSuchElementException | DirectionException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void initialize(String[] params) {
				
	}

}
