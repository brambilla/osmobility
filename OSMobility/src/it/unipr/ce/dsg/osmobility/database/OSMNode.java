package it.unipr.ce.dsg.osmobility.database;

import it.unipr.ce.dsg.osmobility.util.Location;

/**
 * 
 * @author Giacomo Brambilla (giacomo.brambilla@studenti.unipr.it)
 * 
 */

public class OSMNode {

	private Integer id;
	private Location location;

	public OSMNode(Integer id, Location location) {
		this.id = id;
		this.location = location;
	}

	/**
	 * @return id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @return location
	 */
	public Location getLocation() {
		return location;
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
		OSMNode other = (OSMNode) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
