package it.unipr.ce.dsg.osmobility.util;

import it.unipr.ce.dsg.osmobility.database.OSMDirection;
import it.unipr.ce.dsg.osmobility.database.OSMWay;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * 
 * @author Giacomo Brambilla (giacomo.brambilla@studenti.unipr.it)
 *
 */

public class Path implements Iterable<Location> {

	private LinkedHashMap<Location, HashSet<OSMWay>> locations;

	/**
	 * Constructs an empty path.
	 */
	public Path() {
		locations = new LinkedHashMap<Location, HashSet<OSMWay>>();
	}

	/**
	 * Constructs a path containing the locations of the specified collection, in the order they are returned by the map's key set iterator.
	 * @param locations
	 * - a map whose locations are to be placed into this path and the associated ways.
	 * @throws NullPointerException
	 * - if one of the specified collections is null
	 */
	public Path(Map<Location, HashSet<OSMWay>> locations) throws NullPointerException {
		this.locations = new LinkedHashMap<Location, HashSet<OSMWay>>(locations);
	}

	/**
	 * Add locations and relative ways to the path.
	 * @param locations
	 * - locations with relative ways in the path.
	 */
	public void add(Map<Location, HashSet<OSMWay>> locations) {
		if(this.locations.isEmpty()) {
			this.locations.putAll(locations);
		} else {
			for(Location location : locations.keySet()) {
				if(this.locations.containsKey(location)) {
					this.locations.get(location).addAll(locations.get(location));
				} else {
					this.locations.put(location, locations.get(location));
				}
			}
		}
	}

	/**
	 * Returns an iterator over the locations in this path (in proper sequence). This implementation merely returns a path iterator over the path.
	 * @return
	 * an iterator over the locations in this path (in proper sequence)
	 */
	@Override
	public Iterator<Location> iterator() {
		return locations.keySet().iterator();
	}

	/**
	 * Returns true if the path has more locations. (In other words, returns true if nextLocation() would return a location rather than throwing an exception.)
	 * @param location
	 * - the location after which you want evaluate if the path has more locations.
	 * @return
	 * true if the path has more locations.
	 */
	public boolean hasNextLocation(Location location) {
		Iterator<Location> iterator = locations.keySet().iterator();
		boolean hasNextLocation = false;
		while(iterator.hasNext()) {
			if(iterator.next().equals(location)) {
				if(iterator.hasNext()) {
					hasNextLocation = true;
					break;
				} else {
					hasNextLocation = false;
					break;
				}
			}
		}

		return hasNextLocation;
	}

	/**
	 * Returns the next location in the path.
	 * @return
	 * the next location in the path
	 * @throws NoSuchElementException
	 * - if the path has no more locations
	 */
	public Location nextLocation(Location location) {
		Iterator<Location> iterator = locations.keySet().iterator();
		while(iterator.hasNext()) {
			if(iterator.next().equals(location)) {
				if(iterator.hasNext()) {
					return iterator.next();
				} else {
					throw new NoSuchElementException();
				}
			}
		}
		throw new NoSuchElementException();
	}

	/**
	 * Returns the way of a path that covers a location.
	 * @param location
	 * - a location which belongs to a way in a path.
	 * @param nextLocation
	 * - next location in the path.
	 * @return
	 * a way that covers the location in the path.
	 * @throws NoSuchElementException
	 * - if the path has no ways associated to locations.
	 */
	public OSMWay getOSMWay(Location location, Location nextLocation) {
		if(locations.containsKey(location) && locations.containsKey(nextLocation)) {
			HashSet<OSMWay> locationWays = new HashSet<OSMWay>(locations.get(location));
			HashSet<OSMWay> nextLocationWays = new HashSet<OSMWay>(locations.get(nextLocation));
			
			if(locationWays.isEmpty()) {
				throw new NoSuchElementException();
			} else if(locationWays.size() == 1) {
				return locationWays.iterator().next();
			} else {
				if(locationWays.retainAll(nextLocationWays)) {
					if(locationWays.size() == 1) {
						return locationWays.iterator().next();
					} else {
						throw new NoSuchElementException();
					}
				} else {
					throw new NoSuchElementException();
				}
			}
			
		} else {
			throw new NoSuchElementException();
		}
	}
	
	/**
	 * Returns the direction of the way associated to two locations. The direction is determined by the movement from a location to a next location.
	 * @param location
	 * - the location from which you consider the direction in the way.
	 * @param nextLocation
	 * - the location for which you consider the direction in the way.
	 * @return
	 * the direction of the way from the location to the next location in the path or null if no way exists.
	 */
	public OSMDirection getOSMDirection(Location location, Location nextLocation) {
		OSMWay way = getOSMWay(location, nextLocation);
		if(way == null) {
			return null;
		} else {
			return way.getOSMDirection(location, nextLocation);
		}
	}

	/**
	 * Returns the first location in this path.
	 * @return
	 * the first location in this path
	 * @throws NoSuchElementException
	 * if this path is empty.
	 */
	public Location getFirstLocation() throws NoSuchElementException {
		if(locations.isEmpty()) {
			throw new NoSuchElementException();
		} else {
			return locations.keySet().iterator().next();
		}
	}
	
	/**
	 * Returns the last location in this path.
	 * @return
	 * the last location in this path.
	 * @throws NoSuchElementException
	 * if this path is empty.
	 */
	public Location getLastLocation() throws NoSuchElementException {
		if(locations.isEmpty()) {
			throw new NoSuchElementException();
		} else {
			Location lastLocation = null;
			for(Location location : locations.keySet()) {
				lastLocation = location;
			}
			return lastLocation;
		}
	}

	/* (non Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((locations == null) ? 0 : locations.hashCode());
		return result;
	}

	/* (non Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Path other = (Path) obj;
		if (locations == null) {
			if (other.locations != null)
				return false;
		} else if (!locations.equals(other.locations))
			return false;
		return true;
	}

	/**
	 * Returns the ways to which the specified location is mapped, or null if this path contains no mapping for the location. 
	 * More formally, if this path contains a mapping from a location l to ways w such that (location==null ? l==null : location.equals(l)), then this method returns w; otherwise it returns null. (There can be at most one such mapping.)
	 * A return value of null does not necessarily indicate that the path contains no mapping for the location; it's also possible that the path explicitly maps the location to null. 
	 * @param location
	 * - the location whose associated ways are to be returned
	 * @return
	 * the ways to which the specified location is mapped, or null if this path contains no mapping for the location.
	 */
	public HashSet<OSMWay> getWaysMappedToLocation(Location location) {
		return locations.get(location);
	}
	
	/**
	 * Returns the number of locations in this path (its cardinality).
	 * @return
	 * the number of locations in this path (its cardinality)
	 */
	public int size() {
		return locations.size();
	}
	
	/**
	 * Returns the length of this path. The length is equal to the sum of the distances between locations of the path.
	 * @return
	 * the length of the path.
	 */
	public Double length() {
		Double length = 0.0d;
		Location[] locationsArray = locations.keySet().toArray(new Location[locations.keySet().size()]);
		for(int i = 0; i < locationsArray.length - 1; i++) {
			length = length + locationsArray[i].distanceFrom(locationsArray[i+1]);
		}
		return length;
	}
}
