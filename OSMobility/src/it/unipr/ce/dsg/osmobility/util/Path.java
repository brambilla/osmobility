package it.unipr.ce.dsg.osmobility.util;

import it.unipr.ce.dsg.osmobility.database.OSMDirection;
import it.unipr.ce.dsg.osmobility.database.OSMWay;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * 
 * @author Giacomo Brambilla (giacomo.brambilla@studenti.unipr.it)
 *
 */

public class Path implements Iterable<Location> {

	private LinkedList<Location> locations;
	private HashMap<Location, HashSet<OSMWay>> waysMappedToLocations;

	/**
	 * Constructs an empty path.
	 */
	public Path() {
		locations = new LinkedList<Location>();
		waysMappedToLocations = new HashMap<Location, HashSet<OSMWay>>();
	}

	/**
	 * Constructs a path containing the locations of the specified collection, in the order they are returned by the collection's iterator.
	 * @param locations
	 * - the collection whose locations are to be placed into this path
	 * @param waysMappedToLocations
	 * - the map between locations and relative ways
	 * @throws NullPointerException
	 * - if the specified collection or the specified map are null
	 */
	public Path(Collection<Location> locations, Map<Location, HashSet<OSMWay>> waysMappedToLocations) throws NullPointerException {
		if(locations == null || waysMappedToLocations == null) {
			throw new NullPointerException();
		} else {
			this.locations = new LinkedList<Location>(locations);
			this.waysMappedToLocations = new HashMap<Location, HashSet<OSMWay>>(waysMappedToLocations);
		}
	}

	/**
	 * Appends all of the locations in the specified collection to the end of this path, in the order that they are returned by the specified collection's iterator.
	 * The behavior of this operation is undefined if the specified collection is modified while the operation is in progress.
	 * @param locations
	 * - collection containing locations to be added to this path
	 * @param waysMappedToLocations
	 * - the map between locations and relative ways
	 * @throws NullPointerException
	 * - if the specified collection or the specified map are null
	 */
	public void add(Collection<Location> locations, Map<Location, HashSet<OSMWay>> waysMappedToLocations) throws NullPointerException {
		if(locations == null || waysMappedToLocations == null) {
			throw new NullPointerException();
		} else {
			this.locations.addAll(locations);
			for(Location location : waysMappedToLocations.keySet()) {
				if(this.waysMappedToLocations.containsKey(location)) {
					this.waysMappedToLocations.get(location).addAll(waysMappedToLocations.get(location));
				} else {
					this.waysMappedToLocations.put(location, waysMappedToLocations.get(location));
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
		return locations.iterator();
	}

	/**
	 * Returns true if the path has more locations. (In other words, returns true if nextLocation() would return a location rather than throwing an exception.)
	 * @param location
	 * - the location after which you want evaluate if the path has more locations.
	 * @return
	 * true if the path has more locations.
	 */
	public boolean hasNextLocation(Location location) {
		return locations.indexOf(location) < (locations.size() - 1);

	}

	/**
	 * Returns the next location in the path.
	 * @return
	 * the next location in the path
	 * @throws NoSuchElementException
	 * - if the path has no more locations
	 */
	public Location nextLocation(Location location) {
		return locations.get(locations.indexOf(location) + 1);
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
	public OSMWay getOSMWay(Location location, Location nextLocation) throws NoSuchElementException {
		if(locations.contains(location) && locations.contains(nextLocation)) {
			HashSet<OSMWay> locationWays = new HashSet<OSMWay>(waysMappedToLocations.get(location));
			HashSet<OSMWay> nextLocationWays = new HashSet<OSMWay>(waysMappedToLocations.get(nextLocation));

			if(locationWays.isEmpty()) {
				throw new NoSuchElementException();
			} else if(locationWays.size() == 1) {
				return locationWays.iterator().next();
			} else {
				if(locationWays.retainAll(nextLocationWays)) {
					if(locationWays.isEmpty()) {
						return waysMappedToLocations.get(location).iterator().next();
					} else if(locationWays.size() == 1) {
						return locationWays.iterator().next();
					} else {
						return locationWays.iterator().next();
					}
				} else {
					return locationWays.iterator().next();
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
			return locations.getFirst();
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
			return locations.getLast();
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
		return waysMappedToLocations.get(location);
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
		Location[] locationsArray = locations.toArray(new Location[locations.size()]);
		for(int i = 0; i < locationsArray.length - 1; i++) {
			length = length + locationsArray[i].distanceFrom(locationsArray[i+1]);
		}
		return length;
	}

	/**
	 * Returns true if this path contains no locations.
	 * @return
	 * true if this path contains no locations
	 */
	public boolean isEmpty() {
		return locations.isEmpty();
	}

	/**
	 * Concatenates the specified path to the end of this path.
	 * If the size of the argument path is 0, then this Path object is returned.
	 * Otherwise, a new Path object is created, representing a locations sequence that is the concatenation of the locations sequence represented by this Path object and the locations sequence represented by the argument path.
	 * @param path
	 * - the Path that is concatenated to the end of this Path.
	 * @return
	 * a path that represents the concatenation of this object's locations followed by the path argument's locations.
	 */
	public Path concat(Path path) {
		if(isEmpty()) {
			return this;
		} else {
			Path newPath = new Path(locations, waysMappedToLocations);
			newPath.add(path.locations, path.waysMappedToLocations);
			return newPath;
		}
	}

	/**
	 * Returns a new Path that is a subPath of this path.
	 * The subPath begins at the specified beginIndex and extends to the location at index endIndex - 1.
	 * Thus the size of the subPath is endIndex-beginIndex.
	 * @param beginIndex
	 * - the beginning index, inclusive.
	 * @param endIndex
	 * - the ending index, exclusive.
	 * @return
	 * the specified subPath.
	 * @throws IndexOutOfBoundsException
	 * - if the beginIndex is negative, or endIndex is larger than the size of this Path object, or beginIndex is larger than endIndex.
	 */
	public Path subPath(int beginIndex, int endIndex) throws IndexOutOfBoundsException {
		if(beginIndex < 0 || endIndex > size() || beginIndex > endIndex) {
			throw new IndexOutOfBoundsException();
		} else {
			LinkedList<Location> locations = new LinkedList<Location>(this.locations.subList(beginIndex, endIndex));
			HashMap<Location, HashSet<OSMWay>> waysMappedToLocations = new HashMap<Location, HashSet<OSMWay>>();
			for(Location location : locations) {
				waysMappedToLocations.put(location, this.waysMappedToLocations.get(location));
			}
			return new Path(locations, waysMappedToLocations);
		}
	}

	/**
	 * Returns a new Path that is a subPath of this Path.
	 * The subPath begins with the location at the specified index and extends to the end of this Path.
	 * @param beginIndex
	 * - the beginning index, inclusive.
	 * @return
	 * the specified subPath.
	 * @throws IndexOutOfBoundsException
	 * - if beginIndex is negative or larger than the size of this Path object.
	 */
	public Path subPath(int beginIndex) throws IndexOutOfBoundsException {
		if(beginIndex < 0 || beginIndex > size()) {
			throw new IndexOutOfBoundsException();
		} else {
			return subPath(beginIndex, size());
		}
	}

	/**
	 * Returns the index within this Path of the first occurrence of the specified location.
	 * If a location occurs in the location sequence represented by this Path object, then the index of the first such occurrence is returned.
	 * Otherwise, if no such location occurs in this Path, then -1 is returned.
	 * @param location
	 * - a location
	 * @return
	 * the index of the first occurrence of the location in the location sequence represented by this object, or -1 if the location does not occur.
	 */
	public int indexOf(Location location) {
		return locations.indexOf(location);
	}
}
