package it.unipr.ce.dsg.osmobility.database;

import it.unipr.ce.dsg.osmobility.util.Location;
import it.unipr.ce.dsg.osmobility.util.Path;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * 
 * @author Giacomo Brambilla (giacomo.brambilla@studenti.unipr.it)
 *
 */

public class Database {

	private static final String DRIVER_NAME = "org.postgresql.Driver"; 
	private static Connection connection = null;

	/**
	 * Connects to PostGreSQL database.
	 * @param host
	 * - host of the database.
	 * @param db
	 * - name of the database.
	 * @param user
	 * - user of the database.
	 * @param password
	 * - password of the database.
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static void connect(String host, String db, String user, String password) throws SQLException, ClassNotFoundException {
		Class.forName(DRIVER_NAME);
		String url = "jdbc:postgresql://" + host + "/" + db;
		connection = DriverManager.getConnection(url, user, password);
	}

	/**
	 * Disconnects from PostGreSQL database.
	 * @throws SQLException
	 */
	public static void disconnect() throws SQLException {
		connection.close();
	}

	/**
	 * Returns a random node of a random way of the map for the specified vehicle.
	 * @param vehicle
	 * - a vehicle allowed to access the node.
	 * @return
	 * a random node from a random way that allows the access of the specified vehicle.
	 * @throws SQLException
	 */
	public static OSMNode getRandomOSMNode(OSMVehicle vehicle) throws SQLException {
		PreparedStatement s;
		switch(vehicle) {
		case MOTORCAR:
			s = connection.prepareStatement("SELECT source, w.y1, w.x1 FROM ways w JOIN classes c ON w.class_id=c.id WHERE c.name NOT IN ('pedestrian', 'path', 'bridleway', 'cycleway', 'footway') ORDER BY RANDOM() LIMIT 1");
			break;
		case MOTORCYCLE:
			s = connection.prepareStatement("SELECT source, w.y1, w.x1 FROM ways w JOIN classes c ON w.class_id=c.id WHERE c.name NOT IN ('pedestrian', 'path', 'bridleway', 'cycleway', 'footway') ORDER BY RANDOM() LIMIT 1");
			break;
		case BICYCLE:
			s = connection.prepareStatement("SELECT source, w.y1, w.x1 FROM ways w JOIN classes c ON w.class_id=c.id WHERE c.name NOT IN ('motorway', 'pedestrian', 'bridleway', 'footway') ORDER BY RANDOM() LIMIT 1");
			break;
		case FOOT:
			s = connection.prepareStatement("SELECT source, w.y1, w.x1 FROM ways w JOIN classes c ON w.class_id=c.id WHERE c.name NOT IN ('motorway', 'bridleway', 'cycleway') ORDER BY RANDOM() LIMIT 1");
			break;
		case HORSE:
			s = connection.prepareStatement("SELECT source, w.y1, w.x1 FROM ways w JOIN classes c ON w.class_id=c.id WHERE c.name NOT IN ('motorway', 'pedestrian', 'cycleway', 'footway') ORDER BY RANDOM() LIMIT 1");
			break;
		default:
			s = connection.prepareStatement("SELECT source, w.y1, w.x1 FROM ways w JOIN classes c ON w.class_id=c.id WHERE c.name NOT IN ('pedestrian', 'path', 'bridleway', 'cycleway', 'footway') ORDER BY RANDOM() LIMIT 1");
			break;
		}
		ResultSet rs = s.executeQuery();
		OSMNode node = null;
		if (rs.next()) {
			node = new OSMNode(rs.getInt(1), new Location(rs.getDouble(2), rs.getDouble(3)));
		}
		rs.close();
		s.close();
		return node;
	}

	/**
	 * Returns a random node of a random way of the map for the specified vehicle inside a bounding box delimited by the specified coordinates.
	 * @param vehicle
	 * - a vehicle allowed to access the node.
	 * @return
	 * a random node from a random way that allows the access of the specified vehicle.
	 * * @param left
	 * - left-most longitude of the bounding box
	 * @param right
	 * - right-most longitude of the bounding box
	 * @param top
	 * - highest latitude of the bounding box
	 * @param bottom
	 * - lowest latitude of the bounding box
	 * @throws SQLException
	 */
	public static OSMNode getRandomOSMNode(OSMVehicle vehicle, Double left, Double right, Double top, Double bottom) throws SQLException {
		PreparedStatement s;
		switch(vehicle) {
		case MOTORCAR:
			s = connection.prepareStatement("SELECT source, w.y1, w.x1 FROM ways w JOIN classes c ON w.class_id=c.id WHERE c.name NOT IN ('pedestrian', 'path', 'bridleway', 'cycleway', 'footway') AND w.x1 > ? AND w.x1 < ? AND w.y1 < ? AND w.y1 > ? ORDER BY RANDOM() LIMIT 1");
			break;
		case MOTORCYCLE:
			s = connection.prepareStatement("SELECT source, w.y1, w.x1 FROM ways w JOIN classes c ON w.class_id=c.id WHERE c.name NOT IN ('pedestrian', 'path', 'bridleway', 'cycleway', 'footway') AND w.x1 > ? AND w.x1 < ? AND w.y1 < ? AND w.y1 > ? ORDER BY RANDOM() LIMIT 1");
			break;
		case BICYCLE:
			s = connection.prepareStatement("SELECT source, w.y1, w.x1 FROM ways w JOIN classes c ON w.class_id=c.id WHERE c.name NOT IN ('motorway', 'pedestrian', 'bridleway', 'footway') AND w.x1 > ? AND w.x1 < ? AND w.y1 < ? AND w.y1 > ? ORDER BY RANDOM() LIMIT 1");
			break;
		case FOOT:
			s = connection.prepareStatement("SELECT source, w.y1, w.x1 FROM ways w JOIN classes c ON w.class_id=c.id WHERE c.name NOT IN ('motorway', 'bridleway', 'cycleway') AND w.x1 > ? AND w.x1 < ? AND w.y1 < ? AND w.y1 > ? ORDER BY RANDOM() LIMIT 1");
			break;
		case HORSE:
			s = connection.prepareStatement("SELECT source, w.y1, w.x1 FROM ways w JOIN classes c ON w.class_id=c.id WHERE c.name NOT IN ('motorway', 'pedestrian', 'cycleway', 'footway') AND w.x1 > ? AND w.x1 < ? AND w.y1 < ? AND w.y1 > ? ORDER BY RANDOM() LIMIT 1");
			break;
		default:
			s = connection.prepareStatement("SELECT source, w.y1, w.x1 FROM ways w JOIN classes c ON w.class_id=c.id WHERE c.name NOT IN ('pedestrian', 'path', 'bridleway', 'cycleway', 'footway') AND w.x1 > ? AND w.x1 < ? AND w.y1 < ? AND w.y1 > ? ORDER BY RANDOM() LIMIT 1");
			break;
		}
		s.setDouble(1, left);
		s.setDouble(2, right);
		s.setDouble(3, top);
		s.setDouble(4, bottom);
		ResultSet rs = s.executeQuery();
		OSMNode node = null;
		if (rs.next()) {
			node = new OSMNode(rs.getInt(1), new Location(rs.getDouble(2), rs.getDouble(3)));
		}
		rs.close();
		s.close();
		return node;
	}

	/**
	 * Returns a random path for the specified vehicle with the specified resolution.
	 * @param vehicle
	 * - the type vehicle that follows the path.
	 * @param resolution
	 * - maximum resolution of distance between locations of the path.
	 * @return
	 * a random path accessible by the specified vehicle with the specified resolution.
	 */
	public static Path getRandomPath(OSMVehicle vehicle, Double resolution) throws SQLException {
		OSMNode departure = getRandomOSMNode(vehicle);
		OSMNode arrival = getRandomOSMNode(vehicle);

		Path path = getPath(departure, arrival, vehicle, resolution);
		if(path == null) {
			return getRandomPath(vehicle, resolution);
		} else {
			return path;
		}
	}

	/**
	 * Returns a random path for the specified vehicle with the specified resolution inside a bounding box delimited by the specified coordinates.
	 * @param vehicle
	 * - the type vehicle that follows the path
	 * @param resolution
	 * - maximum resolution of distance between locations of the path
	 * @param left
	 * - left-most longitude of the bounding box
	 * @param right
	 * - right-most longitude of the bounding box
	 * @param top
	 * - highest latitude of the bounding box
	 * @param bottom
	 * - lowest latitude of the bounding box
	 * @return
	 * a random path accessible by the specified vehicle with the specified resolution inside the specified bounding box
	 */
	public static Path getRandomPath(OSMVehicle vehicle, Double resolution, Double left, Double right, Double top, Double bottom) throws SQLException {
		OSMNode departure = getRandomOSMNode(vehicle, left, right, top, bottom);
		OSMNode arrival = getRandomOSMNode(vehicle, left, right, top, bottom);

		Path path = getPath(departure, arrival, vehicle, resolution);
		if(path == null) {
			return getRandomPath(vehicle, resolution);
		} else {
			return path;
		}
	}

	/**
	 * Returns a path for the specified vehicle with the specified resolution from departure to arrival.
	 * @param departure
	 * - the location where the path starts.
	 * @param arrival
	 * - the location where the path ends.
	 * @param vehicle
	 * - the type vehicle that follows the path.
	 * @param resolution
	 * - maximum resolution of distance between locations of the path.
	 * @return
	 * a path accessible by the specified vehicle with the specified resolution that starts from departure and ends in arrival or null if no path exists.
	 * @throws SQLException
	 */
	public static Path getPath(Location departure, Location arrival, OSMVehicle vehicle, Double resolution) throws SQLException {
		OSMNode departureOSMNode = getNearestOSMNode(departure, vehicle);
		OSMNode arrivalOSMNode = getNearestOSMNode(arrival, vehicle);

		return getPath(departureOSMNode, arrivalOSMNode, vehicle, resolution);
	}

	/**
	 * Returns a path for the specified vehicle with the specified resolution from departure to arrival.
	 * @param departure
	 * - the OSMNode where the path starts.
	 * @param arrival
	 * - the OSMNode where the path ends.
	 * @param vehicle
	 * - the type vehicle that follows the path.
	 * @param resolution
	 * - maximum resolution of distance between locations of the path.
	 * @return
	 * a path accessible by the specified vehicle with the specified resolution that starts from departure and ends in arrival or null if no path exists.
	 * @throws SQLException
	 */
	public static Path getPath(OSMNode departure, OSMNode arrival, OSMVehicle vehicle, Double resolution) throws SQLException {
		connection.setAutoCommit(false);
		PreparedStatement s1;
		switch(vehicle) {
		case MOTORCAR:
			s1 = connection.prepareStatement("SELECT r.id2 as way_id, r.cost as length, w.source, w.target, w.maxspeed_forward, w.maxspeed_backward, w.y1 as source_latitude, w.x1 as source_longitude, w.y2 as destination_latitude, w.x2 as destination_longitude FROM (pgr_dijkstra('SELECT w.gid AS id, w.source::integer, w.target::integer, w.length::double precision AS cost, w.reverse_cost::double precision as reverse_cost, c.name as class FROM ways w JOIN classes c ON (w.class_id=c.id) WHERE c.name NOT IN (''pedestrian'', ''path'', ''bridleway'', ''cycleway'', ''footway'')', ?, ?, true, true) r JOIN ways w ON r.id2=w.gid) LEFT OUTER JOIN classes c ON w.class_id=c.id ORDER BY r.seq ASC");
			break;
		case MOTORCYCLE:
			s1 = connection.prepareStatement("SELECT r.id2 as way_id, r.cost as length, w.source, w.target, w.maxspeed_forward, w.maxspeed_backward, w.y1 as source_latitude, w.x1 as source_longitude, w.y2 as destination_latitude, w.x2 as destination_longitude FROM (pgr_dijkstra('SELECT w.gid AS id, w.source::integer, w.target::integer, w.length::double precision AS cost, w.reverse_cost::double precision as reverse_cost, c.name as class FROM ways w JOIN classes c ON (w.class_id=c.id) WHERE c.name NOT IN (''pedestrian'', ''path'', ''bridleway'', ''cycleway'', ''footway'')', ?, ?, true, true) r JOIN ways w ON r.id2=w.gid) LEFT OUTER JOIN classes c ON w.class_id=c.id ORDER BY r.seq ASC");
			break;
		case BICYCLE:
			s1 = connection.prepareStatement("SELECT r.id2 as way_id, r.cost as length, w.source, w.target, w.maxspeed_forward, w.maxspeed_backward, w.y1 as source_latitude, w.x1 as source_longitude, w.y2 as destination_latitude, w.x2 as destination_longitude FROM (pgr_dijkstra('SELECT w.gid AS id, w.source::integer, w.target::integer, w.length::double precision AS cost, w.reverse_cost::double precision as reverse_cost, c.name as class FROM ways w JOIN classes c ON (w.class_id=c.id) WHERE c.name NOT IN (''motorway'', ''pedestrian'', ''bridleway'', ''footway'')', ?, ?, true, true) r JOIN ways w ON r.id2=w.gid) LEFT OUTER JOIN classes c ON w.class_id=c.id ORDER BY r.seq ASC");
			break;
		case FOOT:
			s1 = connection.prepareStatement("SELECT r.id2 as way_id, r.cost as length, w.source, w.target, w.maxspeed_forward, w.maxspeed_backward, w.y1 as source_latitude, w.x1 as source_longitude, w.y2 as destination_latitude, w.x2 as destination_longitude FROM (pgr_dijkstra('SELECT w.gid AS id, w.source::integer, w.target::integer, w.length::double precision AS cost, w.reverse_cost::double precision as reverse_cost, c.name as class FROM ways w JOIN classes c ON (w.class_id=c.id) WHERE c.name NOT IN (''motorway'', ''bridleway'', ''cycleway'')', ?, ?, true, true) r JOIN ways w ON r.id2=w.gid) LEFT OUTER JOIN classes c ON w.class_id=c.id ORDER BY r.seq ASC");
			break;
		case HORSE:
			s1 = connection.prepareStatement("SELECT r.id2 as way_id, r.cost as length, w.source, w.target, w.maxspeed_forward, w.maxspeed_backward, w.y1 as source_latitude, w.x1 as source_longitude, w.y2 as destination_latitude, w.x2 as destination_longitude FROM (pgr_dijkstra('SELECT w.gid AS id, w.source::integer, w.target::integer, w.length::double precision AS cost, w.reverse_cost::double precision as reverse_cost, c.name as class FROM ways w JOIN classes c ON (w.class_id=c.id) WHERE c.name NOT IN (''motorway'', ''pedestrian'', ''cycleway'', ''footway'')', ?, ?, true, true) r JOIN ways w ON r.id2=w.gid) LEFT OUTER JOIN classes c ON w.class_id=c.id ORDER BY r.seq ASC");
			break;
		default:
			s1 = connection.prepareStatement("SELECT r.id2 as way_id, r.cost as length, w.source, w.target, w.maxspeed_forward, w.maxspeed_backward, w.y1 as source_latitude, w.x1 as source_longitude, w.y2 as destination_latitude, w.x2 as destination_longitude FROM (pgr_dijkstra('SELECT w.gid AS id, w.source::integer, w.target::integer, w.length::double precision AS cost, w.reverse_cost::double precision as reverse_cost, c.name as class FROM ways w JOIN classes c ON (w.class_id=c.id) WHERE c.name NOT IN (''pedestrian'', ''path'', ''bridleway'', ''cycleway'', ''footway'')', ?, ?, true, true) r JOIN ways w ON r.id2=w.gid) LEFT OUTER JOIN classes c ON w.class_id=c.id ORDER BY r.seq ASC");
			break;
		}
		s1.setInt(1, departure.getId());
		s1.setInt(2, arrival.getId());
		ResultSet rs1 = s1.executeQuery();
		Integer lastSourceId = departure.getId();

		LinkedList<Location> locations = new LinkedList<Location>();
		HashMap<Location, HashSet<OSMWay>> waysMappedToLocations = new HashMap<Location, HashSet<OSMWay>>();

		while(rs1.next()) {

			Integer id = rs1.getInt(1);
			Double length = rs1.getDouble(2); //length in Km
			Integer sourceId = rs1.getInt(3);
			Integer targetId = rs1.getInt(4);
			Double maximumSpeedForward = rs1.getDouble(5); //max speed in Km/h
			Double maximumSpeedBackward = rs1.getDouble(6);
			Double sourceLatitude = rs1.getDouble(7);
			Double sourceLongitude = rs1.getDouble(8);
			Double targetLatitude = rs1.getDouble(9);
			Double targetLongitude = rs1.getDouble(10);
			Double minimumSpeedForward;
			Double minimumSpeedBackward;
			switch(vehicle) {
			case MOTORCAR:
				minimumSpeedForward = 30.0;
				minimumSpeedBackward = 30.0;
				break;
			case MOTORCYCLE:
				minimumSpeedForward = 30.0;
				minimumSpeedBackward = 30.0;
				break;
			case BICYCLE:
				minimumSpeedForward = 10.0;
				minimumSpeedBackward = 10.0;
				break;
			case FOOT:
				minimumSpeedForward = 2.0;
				minimumSpeedBackward = 2.0;
				break;
			case HORSE:
				minimumSpeedForward = 10.0;
				minimumSpeedBackward = 10.0;
				break;
			default:
				minimumSpeedForward = 30.0;
				minimumSpeedBackward = 30.0;
				break;
			}

			OSMNode source = new OSMNode(sourceId, new Location(sourceLatitude, sourceLongitude));
			OSMNode target = new OSMNode(targetId, new Location(targetLatitude, targetLongitude));
			OSMWay way = new OSMWay(id, length, maximumSpeedForward, maximumSpeedBackward, minimumSpeedForward, minimumSpeedBackward, source, target);

			PreparedStatement s2 = connection.prepareStatement("SELECT gid, n, ST_X(ST_PointN(ST_Segmentize(the_geom, ?), n)), ST_Y(ST_PointN(ST_Segmentize(the_geom, ?), n)) FROM ways CROSS JOIN generate_series(1,100) n WHERE n <= ST_NumPoints(ST_Segmentize(the_geom, ?)) and gid=?;");
			s2.setDouble(1, Math.toDegrees(resolution/6370.9860)); //6370.986 is the radius of Earth in Km
			s2.setDouble(2, Math.toDegrees(resolution/6370.9860));
			s2.setDouble(3, Math.toDegrees(resolution/6370.9860));
			s2.setInt(4, id);
			ResultSet rs2 = s2.executeQuery();

			LinkedList<Location> locationsOfTheWay = new LinkedList<Location>();
			while(rs2.next()) {
				Double longitude = rs2.getDouble(3);
				Double latitude = rs2.getDouble(4); 
				locationsOfTheWay.add(new Location(latitude, longitude));
			}
			rs2.close();
			s2.close();

			Iterator<Location> iterator;
			if(lastSourceId.equals(targetId)) {
				iterator = locationsOfTheWay.descendingIterator();
				lastSourceId = sourceId;
			} else {
				iterator = locationsOfTheWay.iterator();
				lastSourceId = targetId;
			}
			while(iterator.hasNext()) {
				Location location = iterator.next();
				if(locations.isEmpty() || !locations.getLast().equals(location)) {
					locations.add(location);
				}
				if(waysMappedToLocations.containsKey(location)) {
					waysMappedToLocations.get(location).add(way);
				} else {
					HashSet<OSMWay> ways = new HashSet<OSMWay>();
					ways.add(way);
					waysMappedToLocations.put(location, ways);
				}
			}
		}

		rs1.close();
		s1.close();
		connection.setAutoCommit(true);

		if(locations.isEmpty()) {
			return null;
		} else {
			return new Path(locations, waysMappedToLocations);
		}
	}

	/**
	 * Returns the nearest OSMNode to the specified location for the specified vehicle.
	 * @param location
	 * - the location for which to search the nearest OSMNode.
	 * @param vehicle
	 *  - the type of vehicle allowed to access the node.
	 * @return
	 * Nearest OSMNode to the specified location.
	 * @throws SQLException
	 */
	public static OSMNode getNearestOSMNode(Location location, OSMVehicle vehicle) throws SQLException {
		PreparedStatement s;
		switch(vehicle) {
		case MOTORCAR:
			s = connection.prepareStatement("SELECT w.source, w.y1, w.x1 FROM ways w JOIN classes c ON w.class_id=c.id WHERE c.name NOT IN ('pedestrian', 'path', 'bridleway', 'cycleway', 'footway') ORDER BY ST_Distance_Sphere(ST_SetSRID(ST_MakePoint(w.x1, w.y1), 4326), ST_SetSRID(ST_MakePoint(?, ?), 4326)) ASC LIMIT 1");
			break;
		case MOTORCYCLE:
			s = connection.prepareStatement("SELECT w.source, w.y1, w.x1 FROM ways w JOIN classes c ON w.class_id=c.id WHERE c.name NOT IN ('pedestrian', 'path', 'bridleway', 'cycleway', 'footway') ORDER BY ST_Distance_Sphere(ST_SetSRID(ST_MakePoint(w.x1, w.y1), 4326), ST_SetSRID(ST_MakePoint(?, ?), 4326)) ASC LIMIT 1");
			break;
		case BICYCLE:
			s = connection.prepareStatement("SELECT w.source, w.y1, w.x1 FROM ways w JOIN classes c ON w.class_id=c.id WHERE c.name NOT IN ('motorway', 'pedestrian', 'bridleway', 'footway') ORDER BY ST_Distance_Sphere(ST_SetSRID(ST_MakePoint(w.x1, w.y1), 4326), ST_SetSRID(ST_MakePoint(?, ?), 4326)) ASC LIMIT 1");
			break;
		case FOOT:
			s = connection.prepareStatement("SELECT w.source, w.y1, w.x1 FROM ways w JOIN classes c ON w.class_id=c.id WHERE c.name NOT IN ('motorway', 'bridleway', 'cycleway') ORDER BY ST_Distance_Sphere(ST_SetSRID(ST_MakePoint(w.x1, w.y1), 4326), ST_SetSRID(ST_MakePoint(?, ?), 4326)) ASC LIMIT 1");
			break;
		case HORSE:
			s = connection.prepareStatement("SELECT w.source, w.y1, w.x1 FROM ways w JOIN classes c ON w.class_id=c.id WHERE c.name NOT IN ('motorway', 'pedestrian', 'cycleway', 'footway') ORDER BY ST_Distance_Sphere(ST_SetSRID(ST_MakePoint(w.x1, w.y1), 4326), ST_SetSRID(ST_MakePoint(?, ?), 4326)) ASC LIMIT 1");
			break;
		default:
			s = connection.prepareStatement("SELECT w.source, w.y1, w.x1 FROM ways w JOIN classes c ON w.class_id=c.id WHERE c.name NOT IN ('pedestrian', 'path', 'bridleway', 'cycleway', 'footway') ORDER BY ST_Distance_Sphere(ST_SetSRID(ST_MakePoint(w.x1, w.y1), 4326), ST_SetSRID(ST_MakePoint(?, ?), 4326)) ASC LIMIT 1");
			break;
		}
		s.setDouble(1, location.getLongitude());
		s.setDouble(2, location.getLatitude());
		ResultSet rs = s.executeQuery();
		OSMNode nearestNode = null;
		if (rs.next()) {
			nearestNode = new OSMNode(rs.getInt(1), new Location(rs.getDouble(2), rs.getDouble(3)));
		}
		rs.close();
		s.close();
		return nearestNode;
	}

	/**
	 * Returns a random path for the specified vehicle with the specified resolution from departure.
	 * @param departure
	 * - the location where the path starts.
	 * @param vehicle
	 * - the type vehicle that follows the path.
	 * @param resolution
	 * - maximum resolution of distance between locations of the path.
	 * @return
	 * a path accessible by the specified vehicle with the specified resolution that starts from departure and ends in a random location or null if no path exists.
	 * @throws SQLException
	 */
	public static Path getRandomPath(Location departure, OSMVehicle vehicle, Double resolution) throws SQLException {
		OSMNode departureOSMNode = getNearestOSMNode(departure, vehicle);
		OSMNode arrivalOSMNode = getRandomOSMNode(vehicle);

		return getPath(departureOSMNode, arrivalOSMNode, vehicle, resolution);
	}

	/**
	 * Returns a random path for the specified vehicle with the specified resolution from departure inside a bounding box delimited by the specified coordinates.
	 * @param departure
	 * - the location where the path starts.
	 * @param vehicle
	 * - the type vehicle that follows the path.
	 * @param resolution
	 * - maximum resolution of distance between locations of the path.
	 *  @param left
	 * - left-most longitude of the bounding box
	 * @param right
	 * - right-most longitude of the bounding box
	 * @param top
	 * - highest latitude of the bounding box
	 * @param bottom
	 * - lowest latitude of the bounding box
	 * @return
	 * a path accessible by the specified vehicle with the specified resolution that starts from departure and ends in a random location inside the specified bounding box or null if no path exists.
	 * @throws SQLException
	 */
	public static Path getRandomPath(Location departure, OSMVehicle vehicle, Double resolution, Double left, Double right, Double top, Double bottom) throws SQLException {
		OSMNode departureOSMNode = getNearestOSMNode(departure, vehicle);
		OSMNode arrivalOSMNode = getRandomOSMNode(vehicle, left, right, top, bottom);

		return getPath(departureOSMNode, arrivalOSMNode, vehicle, resolution);
	}
}
