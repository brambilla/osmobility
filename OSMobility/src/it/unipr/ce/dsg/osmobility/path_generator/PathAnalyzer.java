package it.unipr.ce.dsg.osmobility.path_generator;

import it.unipr.ce.dsg.osmobility.database.OSMNode;
import it.unipr.ce.dsg.osmobility.database.OSMWay;
import it.unipr.ce.dsg.osmobility.util.Location;
import it.unipr.ce.dsg.osmobility.util.Path;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.zip.ZipFile;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PathAnalyzer {

	private static HashSet<Path> pathsSet;

	public static void main(String[] args) {
		if(args.length > 0) {
			System.out.println("Reading paths set from " + args[0] + "…");
			readPathsSetFromFile(args[0]);
			System.out.println("Paths set correctly read.");
			System.out.println("Starting analysis of the paths set…");
			analyzePathsSet();
			System.out.println("Analysis completed.");
		} else {
			System.out.println("Please, specify a valid path file");
		}
	}

	private static void analyzePathsSet() {
		Integer numberOfPaths = 0;
		
		Double minimumPathsLength = Double.POSITIVE_INFINITY;
		Double maximumPathsLength = Double.NEGATIVE_INFINITY;
		Double totalPathsLength = 0.0;
		Double averagePathsLength = 0.0;

		Integer minimumPathsSize = Integer.MAX_VALUE;
		Integer maximumPathsSize = Integer.MIN_VALUE;
		Integer totalPathsSize = 0;
		Double averagePathsSize = 0.0;

		numberOfPaths = pathsSet.size();
		for(Path path : pathsSet) {
			Double pathLength = path.length();

			if(pathLength > maximumPathsLength) {
				maximumPathsLength = pathLength;
			}

			if(pathLength < minimumPathsLength) {
				minimumPathsLength = pathLength;
			}

			totalPathsLength = totalPathsLength + pathLength;

			Integer pathSize = path.size();
			
			if(pathSize > maximumPathsSize) {
				maximumPathsSize = pathSize;
			}
			
			if(pathSize < minimumPathsSize) {
				minimumPathsSize = pathSize;
			}
			
			totalPathsSize = totalPathsSize + pathSize;
		}

		averagePathsLength = totalPathsLength / (double) numberOfPaths;
		averagePathsSize = totalPathsSize / (double) numberOfPaths;

		System.out.println("number of paths: " + numberOfPaths);
		
		System.out.println("minimum paths length: " + minimumPathsLength + " km");
		System.out.println("maximum paths length: " + maximumPathsLength + " km");
		System.out.println("total paths length: " + totalPathsLength + " km");
		System.out.println("average paths length: " + averagePathsLength + " km");
		
		System.out.println("minimum paths size: " + minimumPathsSize);
		System.out.println("maximum paths size: " + maximumPathsSize);
		System.out.println("total paths size: " + totalPathsSize);
		System.out.println("average paths size: " + averagePathsSize);
	}

	private static void readPathsSetFromFile(String name) {
		try { 		
			ZipFile zipFile = new ZipFile(name);
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
			pathsSet = new HashSet<Path>();
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

		} catch (IOException e) { 		
			e.printStackTrace();
			System.exit(1);
		}
	}

}
