package it.unipr.ce.dsg.osmobility.util;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.osmobility.exception.PathNotFoundException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * 
 * @author Giacomo Brambilla (giacomo.brambilla@studenti.unipr.it)
 * 
 */

public class PathsSet implements Iterable<Path> {

	private static PathsSet instance = null;
	private HashSet<Path> paths;

	private PathsSet() {
		paths = new HashSet<Path>();
	}

	private PathsSet(int initialCapacity) {
		paths = new HashSet<Path>(initialCapacity);
	}
	
	private PathsSet(Collection<Path> paths) {
		this.paths = new HashSet<Path>(paths);
	}

	/**
	 * Constructs a new, empty set of paths.
	 * @return
	 * An empty set of path.
	 */
	public static synchronized PathsSet getInstance() {
		if (instance == null) {
			instance = new PathsSet();
		}
		return instance;
	}

	/**
	 * Constructs a new, empty set of paths; the instance has the specified initial capacity and default load factor (0.75).
	 * @param initialCapacity
	 * - the initial capacity of the set.
	 * @return
	 * An empty set of paths with the specified initial capacity.
	 * @throws IllegalArgumentException
	 * - if the initial capacity is less than zero
	 */
	public static synchronized PathsSet getInstance(int initialCapacity) throws IllegalArgumentException {
		if(initialCapacity < 0) {
			throw new IllegalArgumentException();
		} else {
			if (instance == null) {
				instance = new PathsSet();
			}
			return instance;
		}
	}
	
	/**
	 * Constructs a new set containing the paths in the specified collection. The instance is created with default load factor (0.75) and an initial capacity sufficient to contain the elements in the specified collection.
	 * @param paths
	 * - the collection whose paths are to be placed into this set
	 * @return
	 * A set containing the specified paths.
	 * @throws NullPointerException
	 * - if the specified collection is null
	 */
	public static synchronized PathsSet getInstance(Collection<Path> paths) throws NullPointerException {
		if(paths ==  null) {
			throw new NullPointerException();
		} else {
			if (instance == null) {
				instance = new PathsSet(paths);
			}
			return instance;
		}
	}

	/**
	 * Returns a random path from the set.
	 * @return
	 * A path chosen randomly from the set.
	 * @throws PathNotFoundException
	 * If the set of paths is empty.
	 */
	public Path getRandomPath() throws PathNotFoundException {
		int index = Engine.getDefault().getSimulationRandom().nextInt(paths.size());
		int i = 0;
		for(Path path : paths) {
			if(i==index) {
				return path;
			}
			i++;
		}
		throw new PathNotFoundException("Exception getting a random path");
	}

	/**
	 * Adds the specified path to this set if it is not already present. More formally, adds the specified path p to this set if this set contains no path p2 such that (p==null ? p2==null : p.equals(p2)). If this set already contains the path, the call leaves the set unchanged and returns false.
	 * @param path
	 * - path to be added to this set.
	 * @return
	 * true if this set did not already contain the specified path.
	 */
	public boolean add(Path path) {
		return paths.add(path);
	}
	
	/**
	 * Returns the number of paths in this set (its cardinality).
	 * @return
	 * the number of paths in this set (its cardinality)
	 */
	public int size() {
		return paths.size();
	}

	@Override
	public Iterator<Path> iterator() {
		return paths.iterator();
	}

}
