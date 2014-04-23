package it.unipr.ce.dsg.osmobility.mobility.event;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Properties;
import java.util.TreeMap;
import java.util.zip.ZipFile;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.osmobility.database.OSMNode;
import it.unipr.ce.dsg.osmobility.database.OSMWay;
import it.unipr.ce.dsg.osmobility.exception.PathNotFoundException;
import it.unipr.ce.dsg.osmobility.mobility.model.MobilityModel;
import it.unipr.ce.dsg.osmobility.mobility.model.MobilityModelFactory;
import it.unipr.ce.dsg.osmobility.mobility.node.MobileNode;
import it.unipr.ce.dsg.osmobility.path_generator.Constants;
import it.unipr.ce.dsg.osmobility.util.Location;
import it.unipr.ce.dsg.osmobility.util.Path;
import it.unipr.ce.dsg.osmobility.util.PathsSet;

/**
 * 
 * @author Giacomo Brambilla (giacomo.brambilla@studenti.unipr.it)
 *
 */

public abstract class MobileNodeBirthEvent extends GeoNodeBirthEvent {

	private MobilityModel mobilityModel;
	private String pathsParam;
	private PathsSet pathsSet;

	public MobileNodeBirthEvent(String id, Properties params, Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);

		TreeMap<Integer, String> mobilityModelParams = new TreeMap<Integer, String>();
		for(String propertyName : params.stringPropertyNames()) {
			if(propertyName.contains("mobilityModel#")) {
				Integer paramIndex = Integer.valueOf(propertyName.substring(propertyName.indexOf("#param") + "#param".length()));
				mobilityModelParams.put(paramIndex, params.getProperty(propertyName));
			}
		}

		String mobilityModelParam = params.getProperty("mobilityModel");
		mobilityModel = MobilityModelFactory.createMobilityModel(mobilityModelParam);
		if(mobilityModelParams.isEmpty()) {
			mobilityModel.initialize(mobilityModelParams.values().toArray(new String[mobilityModelParams.size()]));
		}

		pathsSet = PathsSet.getInstance();

		pathsParam = params.getProperty("paths");

		try {
			readPathsSetFromFile();
		} catch(IOException e) {
			throw new InvalidParamsException("Error in paths param: " + pathsParam);
		}
	}

	private void readPathsSetFromFile() throws IOException {
		ZipFile zipFile = new ZipFile(pathsParam);
		InputStream pathsInputStream = zipFile.getInputStream(zipFile.getEntry(Constants.PATHS_ENTRY_NAME));
		String pathsString = "";
		BufferedReader pathsBufferedReader = new BufferedReader(new InputStreamReader(pathsInputStream));
		String line;
		while((line = pathsBufferedReader.readLine()) != null) {
			pathsString = pathsString + line;
		}
		pathsBufferedReader.close();
		pathsInputStream.close();

		InputStream waysInputStream = zipFile.getInputStream(zipFile.getEntry(Constants.WAYS_ENTRY_NAME));
		String waysString = "";
		BufferedReader waysBufferedReader = new BufferedReader(new InputStreamReader(waysInputStream));
		while((line = waysBufferedReader.readLine()) != null) {
			waysString = waysString + line;
		}
		waysBufferedReader.close();
		waysInputStream.close();

		InputStream nodesInputStream = zipFile.getInputStream(zipFile.getEntry(Constants.NODES_ENTRY_NAME));
		String nodesString = "";
		BufferedReader nodesBufferedReader = new BufferedReader(new InputStreamReader(nodesInputStream));
		while((line = nodesBufferedReader.readLine()) != null) {
			nodesString = nodesString + line;
		}
		nodesBufferedReader.close();
		nodesInputStream.close();

		zipFile.close();

		JsonParser jsonParser = new JsonParser();
		HashMap<Integer, OSMNode> nodes = new HashMap<Integer, OSMNode>();
		HashMap<Integer, OSMWay> ways = new HashMap<Integer, OSMWay>();

		for(JsonElement nodeElement : jsonParser.parse(nodesString).getAsJsonObject().getAsJsonArray(Constants.NODES)) {
			OSMNode node = new OSMNode(nodeElement.getAsJsonObject().get(Constants.NODE_ID).getAsInt(), new Location(nodeElement.getAsJsonObject().get(Constants.NODE_LATITUDE).getAsDouble(), nodeElement.getAsJsonObject().get(Constants.NODE_LONGITUDE).getAsDouble()));
			nodes.put(nodeElement.getAsJsonObject().get(Constants.NODE_ID).getAsInt(), node);
		}

		for(JsonElement wayElement : jsonParser.parse(waysString).getAsJsonObject().getAsJsonArray(Constants.WAYS)) {
			OSMNode source = nodes.get(wayElement.getAsJsonObject().get(Constants.WAY_SOURCE_ID).getAsInt());
			OSMNode target = nodes.get(wayElement.getAsJsonObject().get(Constants.WAY_TARGET_ID).getAsInt());
			OSMWay way = new OSMWay(wayElement.getAsJsonObject().get(Constants.WAY_ID).getAsInt(), wayElement.getAsJsonObject().get(Constants.WAY_LENGTH).getAsDouble(), wayElement.getAsJsonObject().get(Constants.WAY_MAXIMUM_SPEED_FORWARD).getAsDouble(), wayElement.getAsJsonObject().get(Constants.WAY_MAXIMUM_SPEED_BACKWARD).getAsDouble(), wayElement.getAsJsonObject().get(Constants.WAY_MINIMUM_SPEED_FORWARD).getAsDouble(), wayElement.getAsJsonObject().get(Constants.WAY_MINIMUM_SPEED_BACKWARD).getAsDouble(), source, target);

			ways.put(wayElement.getAsJsonObject().get(Constants.WAY_ID).getAsInt(), way);
		}

		for(JsonElement pathElement : jsonParser.parse(pathsString).getAsJsonObject().getAsJsonArray(Constants.PATHS)) {
			Path path = new Path();
			LinkedList<Location> locations = new LinkedList<Location>();
			HashMap<Location, HashSet<OSMWay>> waysMappedToLocations = new HashMap<Location, HashSet<OSMWay>>();

			for(JsonElement locationElement : pathElement.getAsJsonObject().getAsJsonArray(Constants.PATH_LOCATIONS)) {
				JsonObject locationObject = locationElement.getAsJsonObject();
				Location location = new Location(locationObject.get(Constants.PATH_LOCATION_LATITUDE).getAsDouble(), locationObject.get(Constants.PATH_LOCATION_LONGITUDE).getAsDouble());

				HashSet<OSMWay> waysSet = new HashSet<OSMWay>();
				for(JsonElement wayElement : locationObject.getAsJsonArray(Constants.PATH_WAYS_IDS)) {
					waysSet.add(ways.get(wayElement.getAsJsonObject().get(Constants.WAY_ID).getAsInt()));
				}

				locations.add(location);
				waysMappedToLocations.put(location, waysSet);
			}

			path.add(locations, waysMappedToLocations);

			pathsSet.add(path);
		}
	}

	@Override
	public void run() throws RunException {
		super.run();
		try {
			MobileNode mobileNode = (MobileNode) associatedNode;
			Path path = pathsSet.getRandomPath();
			mobileNode.initMobility(path, mobilityModel);
			mobileNode.scheduleNextMove();
		} catch (PathNotFoundException e) {
			throw new RunException("Error in path.");
		}
	}
}
