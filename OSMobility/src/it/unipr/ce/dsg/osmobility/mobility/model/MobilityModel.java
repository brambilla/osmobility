package it.unipr.ce.dsg.osmobility.mobility.model;

import it.unipr.ce.dsg.osmobility.util.Location;
import it.unipr.ce.dsg.osmobility.util.Path;

/**
 * 
 * @author Giacomo Brambilla (giacomo.brambilla@studenti.unipr.it)
 *
 */

public abstract class MobilityModel {
	
	/**
	 * Evaluates the move considering actual location, next location and path of the mobile node and returns the time necessary to the move.
	 * @param key
	 * - key of the mobile node.
	 * @param location
	 * - actual location of the mobile node.
	 * @param nextLocation
	 * - next location of the mobile node.
	 * @param path
	 * - path of the mobile node.
	 * @return
	 * a Double representing the time necessary to the move (in millisecond).
	 */
	public abstract Double evaluateMove(Integer key, Location location, Location nextLocation, Path path);
	
	/**
	 * Initializes the mobility model with parameters specified in the configuration. The order of the parameters in the array is specified in XML file configuration.
	 * @param params
	 * - parameters specified in the configuration.
	 */
	public abstract void initialize(String[] params);

}
