package it.unipr.ce.dsg.osmobility.mobility.model;

import java.util.HashMap;
import java.util.HashSet;

import it.unipr.ce.dsg.osmobility.database.OSMDirection;
import it.unipr.ce.dsg.osmobility.database.OSMWay;
import it.unipr.ce.dsg.osmobility.exception.DirectionException;
import it.unipr.ce.dsg.osmobility.util.Location;
import it.unipr.ce.dsg.osmobility.util.Path;

/**
 * 
 * @author Giacomo Brambilla (giacomo.brambilla@studenti.unipr.it)
 *
 */

public class FluidTrafficModel extends MobilityModel {
	
	private HashMap<OSMWay, HashSet<Integer>> mobileNodesPerWay;
	private static final Double k_jam = 0.25;
	
	public FluidTrafficModel() {
		mobileNodesPerWay = new HashMap<OSMWay, HashSet<Integer>>();
	}
	
	@Override
	public synchronized Double evaluateMove(Integer mobileNodeKey, Location mobileNodeLocation, Location mobileNodeNextLocation, Path mobileNodePath) {
		OSMWay mobileNodeWay = mobileNodePath.getOSMWay(mobileNodeLocation, mobileNodeNextLocation);
		OSMDirection mobileNodeWayDirection = mobileNodePath.getOSMDirection(mobileNodeLocation, mobileNodeNextLocation);
		
		HashSet<OSMWay> emptyWays = new HashSet<OSMWay>();
		for(OSMWay way : mobileNodesPerWay.keySet()) {
			HashSet<Integer> keys = mobileNodesPerWay.get(way);
			keys.remove(mobileNodeKey);
			if(keys.isEmpty()) {
				emptyWays.add(way);
			}
		}
		for(OSMWay way : emptyWays) {
			mobileNodesPerWay.remove(way);
		}
		emptyWays.clear();
		
		if(mobileNodesPerWay.containsKey(mobileNodeWay)) {
			mobileNodesPerWay.get(mobileNodeWay).add(mobileNodeKey);
		} else {
			HashSet<Integer> keys = new HashSet<Integer>();
			keys.add(mobileNodeKey);
			mobileNodesPerWay.put(mobileNodeWay, keys);
		}
		
		Double k = mobileNodesPerWay.get(mobileNodeWay).size()/(mobileNodeWay.getLength()*1000.0);
		try {
			Double speed = Math.max(mobileNodeWay.getMinimumSpeed(mobileNodeWayDirection), mobileNodeWay.getMaximumSpeed(mobileNodeWayDirection)*(1-(k/k_jam)));
			return mobileNodeWay.getLength()/speed*60.0*60.0*1000.0;
		} catch (DirectionException e) {
			e.printStackTrace();
			return 0.0d;
		}
	}

	@Override
	public void initialize(String[] params) {
		
	}

}
