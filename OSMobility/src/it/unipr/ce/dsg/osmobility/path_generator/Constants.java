package it.unipr.ce.dsg.osmobility.path_generator;

/**
 * 
 * @author Giacomo Brambilla (giacomo.brambilla@studenti.unipr.it)
 * 
 */

public class Constants {
	
	public static final String FILENAME_EXTENSION = ".zip";
	public static final String PATHS_ENTRY_NAME = "paths.json";
	public static final String WAYS_ENTRY_NAME = "ways.json";
	public static final String NODES_ENTRY_NAME = "nodes.json";

	public static final String PATHS = "p";
	public static final String PATH_LOCATIONS = "p_l";
	public static final String PATH_LOCATION_LATITUDE = "p_l_la";
	public static final String PATH_LOCATION_LONGITUDE = "p_l_lo";
	public static final String PATH_WAYS_IDS = "p_w";

	public static final String WAYS = "w";
	public static final String WAY_ID = "w_i";
	public static final String WAY_LENGTH = "w_l";
	public static final String WAY_MAXIMUM_SPEED_FORWARD = "w_maxsf";
	public static final String WAY_MAXIMUM_SPEED_BACKWARD = "w_maxsb";
	public static final String WAY_MINIMUM_SPEED_FORWARD = "w_minsf";
	public static final String WAY_MINIMUM_SPEED_BACKWARD = "w_minsb";
	public static final String WAY_SOURCE_ID = "w_s_i";
	public static final String WAY_TARGET_ID = "w_t_i";

	public static final String NODES = "n";
	public static final String NODE_ID = "n_i";
	public static final String NODE_LATITUDE = "n_la";
	public static final String NODE_LONGITUDE = "n_lo";
}
