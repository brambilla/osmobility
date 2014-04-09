package it.unipr.ce.dsg.osmobility.database;

import it.unipr.ce.dsg.osmobility.exception.DirectionException;
import it.unipr.ce.dsg.osmobility.util.Location;

/**
 * 
 * @author Giacomo Brambilla (giacomo.brambilla@studenti.unipr.it)
 *
 */

public class OSMWay {
	
	private Integer id;
	private Double length;
	private Double maximumSpeedForward;
	private Double maximumSpeedBackward;
	private Double minimumSpeedForward;
	private Double minimumSpeedBackward;
	private OSMNode source;
	private OSMNode target;
	
	public OSMWay(Integer id, Double length, Double maximumSpeedForward, Double maximumSpeedBackward, Double minimumSpeedForward, Double minimumSpeedBackward, OSMNode source, OSMNode target) {
		this.id = id;
		this.length = length;
		this.maximumSpeedForward = maximumSpeedForward;
		this.maximumSpeedBackward = maximumSpeedBackward;
		this.minimumSpeedForward = minimumSpeedForward;
		this.minimumSpeedBackward = minimumSpeedBackward;
		this.source = source;
		this.target = target;
	}

	/**
	 * Returns minimum speed of the way in the particular direction.
	 * @param direction
	 * - the direction of the way.
	 * @return minimum speed
	 * minimum speed of the way in the particular direction. (In Km/h)
	 * @throws DirectionException
	 * if the direction is not FORWARD nor BACKWARD.
	 */
	public Double getMinimumSpeed(OSMDirection direction) throws DirectionException {
		if(direction.equals(OSMDirection.FORWARD)) {
			return minimumSpeedForward;
		} else if(direction.equals(OSMDirection.BACKWARD)) {
			return minimumSpeedBackward;
		} else {
			throw new DirectionException("Unexpected direction");
		}
	}

	/**
	 * Returns maximum speed of the way in the particular direction.
	 * @param direction
	 * - the direction of the way.
	 * @return maximum speed
	 * maximum speed of the way in the particular direction. (In Km/h)
	 * @throws DirectionException
	 * if the direction is not FORWARD nor BACKWARD.
	 */
	public Double getMaximumSpeed(OSMDirection direction) throws DirectionException {
		if(direction.equals(OSMDirection.FORWARD)) {
			return maximumSpeedForward;
		} else if(direction.equals(OSMDirection.BACKWARD)) {
			return maximumSpeedBackward;
		} else {
			throw new DirectionException("Unexpected direction");
		}
	}

	/**
	 * Returns the length of this OSMWay in Km.
	 * @return
	 * length of this OSMWay in Km.
	 */
	public Double getLength() {
		return length;
	}

	/**
	 * 
	 * @return id
	 */
	public Integer getId() {
		return id;
	}
	
	/**
	 * @return source
	 */
	public OSMNode getSource() {
		return source;
	}

	/**
	 * 
	 * @return target
	 */
	public OSMNode getTarget() {
		return target;
	}
	
	/* (non Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		OSMWay other = (OSMWay) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	/**
	 * Returns the direction of the way associated to two locations. The direction is determined by the movement from a location to a next location.
	 * @param location
	 * - the location from which you consider the direction in the way.
	 * @param nextLocation
	 * - the location for which you consider the direction in the way.
	 * @return
	 * the direction of the way from the location to the next location.
	 */
	public OSMDirection getOSMDirection(Location location, Location nextLocation) {
		if(location.distanceFrom(source.getLocation()) <= nextLocation.distanceFrom(source.getLocation())) {
			return OSMDirection.FORWARD;
		} else {
			return OSMDirection.BACKWARD;
		}
	}
}
