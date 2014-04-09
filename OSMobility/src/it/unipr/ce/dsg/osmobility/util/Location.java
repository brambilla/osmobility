package it.unipr.ce.dsg.osmobility.util;

import com.google.gson.JsonObject;

/**
 * 
 * @author Giacomo Brambilla (giacomo.brambilla@studenti.unipr.it)
 * 
 */

public class Location {

	private Double latitude;
	private Double longitude;
	private static final Double EARTH_RADIUS = 6370.9860; //in Km

	public Location(Double latitude, Double longitude) {
		this.setLatitude(latitude);
		this.setLongitude(longitude);
	}

	public Location(Location location) {
		this.latitude = location.getLatitude();
		this.longitude = location.getLongitude();
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	@Override
	public String toString() {
		return latitude + ", " + longitude;
	}
	
	/* (non Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((latitude == null) ? 0 : latitude.hashCode());
		result = prime * result
				+ ((longitude == null) ? 0 : longitude.hashCode());
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
		Location other = (Location) obj;
		if (latitude == null) {
			if (other.latitude != null)
				return false;
		} else if (!latitude.equals(other.latitude))
			return false;
		if (longitude == null) {
			if (other.longitude != null)
				return false;
		} else if (!longitude.equals(other.longitude))
			return false;
		return true;
	}

	/**
	 * @param location
	 * - the location with which to compare.
	 * @return
	 * the great-circle distance in Km.
	 */
	public Double distanceFrom(Location location) {
		Double deltaLatitude = latitude - location.getLatitude();
		Double deltaLongitude = longitude - location.getLongitude();
		
		Double a = Math.sin(Math.toRadians(deltaLatitude)/2) * Math.sin(Math.toRadians(deltaLatitude)/2) + 
				Math.sin(Math.toRadians(deltaLongitude)/2) * Math.sin(Math.toRadians(deltaLongitude)/2) * Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(location.getLatitude()));
		
		return 2 * EARTH_RADIUS * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
	}
	
	/**
	 * Indicates whether some other location is "equal to" this one.
	 * @param location
	 * - the reference location with which to compare.
	 * @return
	 * true if this location is the same as the location argument; false otherwise.
	 */
	public boolean equals(Location location) {
		return this.latitude.equals(location.getLatitude()) && this.longitude.equals(location.getLongitude());
	}

	/**
	 * 
	 * @return
	 * A JsonObject that represents the Location.
	 */
	public JsonObject toJsonObject() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("latitude", latitude);
		jsonObject.addProperty("longitude", longitude);
		return jsonObject;
	}
}