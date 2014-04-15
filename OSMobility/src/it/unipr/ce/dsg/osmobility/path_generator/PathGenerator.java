package it.unipr.ce.dsg.osmobility.path_generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import it.unipr.ce.dsg.osmobility.database.Database;
import it.unipr.ce.dsg.osmobility.database.OSMDirection;
import it.unipr.ce.dsg.osmobility.database.OSMVehicle;
import it.unipr.ce.dsg.osmobility.database.OSMWay;
import it.unipr.ce.dsg.osmobility.exception.DirectionException;
import it.unipr.ce.dsg.osmobility.util.Location;
import it.unipr.ce.dsg.osmobility.util.Path;

/**
 * 
 * @author Giacomo Brambilla (giacomo.brambilla@studenti.unipr.it)
 * 
 */

public class PathGenerator {

	private static String host = "";
	private static String db = "";
	private static String user = "";
	private static String password = "";
	private static OSMVehicle vehicle = OSMVehicle.MOTORCAR;
	private static Double resolution = 1.0;
	private static String filename = "";
	private static PathGeneratorMode mode = PathGeneratorMode.RANDOM;
	private static Integer numberOfPaths = 100;
	private static HashSet<Location> switchstations;
	private static Double pathsMinimumLength = 0.0d;

	private static HashSet<Path> pathsSet;

	public static void main(String[] args) {
		readArgs(args);
		pathsSet = new HashSet<Path>(numberOfPaths);
		generatePaths();
		writePathsOnFile();
	}

	private static void writePathsOnFile() {
		System.out.println("Creating paths file…");

		JsonObject jsonPathsSet = new JsonObject();
		JsonObject jsonWaysSet = new JsonObject();
		JsonObject jsonNodesSet = new JsonObject();

		JsonArray jsonPaths = new JsonArray();
		JsonArray jsonWays = new JsonArray();
		JsonArray jsonNodes = new JsonArray();

		HashSet<Integer> wayIds = new HashSet<Integer>();
		HashSet<Integer> nodeIds = new HashSet<Integer>();

		for(Path path : pathsSet) {
			JsonObject jsonPath = new JsonObject();
			JsonArray jsonLocations = new JsonArray();

			for(Location location : path) {
				JsonObject jsonLocation = new JsonObject();
				JsonArray jsonWayIds = new JsonArray();

				jsonLocation.addProperty(Constants.PATH_LOCATION_LATITUDE, location.getLatitude());
				jsonLocation.addProperty(Constants.PATH_LOCATION_LONGITUDE, location.getLongitude());

				for(OSMWay way : path.getWaysMappedToLocation(location)) {
					JsonObject jsonWayId = new JsonObject();

					jsonWayId.addProperty(Constants.WAY_ID, way.getId());

					if(!wayIds.contains(way.getId())) {
						wayIds.add(way.getId());

						JsonObject jsonWay = new JsonObject();
						jsonWay.addProperty(Constants.WAY_ID, way.getId());
						jsonWay.addProperty(Constants.WAY_LENGTH, way.getLength());
						try {
							jsonWay.addProperty(Constants.WAY_MAXIMUM_SPEED_FORWARD, way.getMaximumSpeed(OSMDirection.FORWARD));
							jsonWay.addProperty(Constants.WAY_MAXIMUM_SPEED_BACKWARD, way.getMaximumSpeed(OSMDirection.BACKWARD));
							jsonWay.addProperty(Constants.WAY_MINIMUM_SPEED_FORWARD, way.getMinimumSpeed(OSMDirection.FORWARD));
							jsonWay.addProperty(Constants.WAY_MINIMUM_SPEED_BACKWARD, way.getMinimumSpeed(OSMDirection.BACKWARD));
						} catch (DirectionException e) {
							e.printStackTrace();
						}

						jsonWay.addProperty(Constants.WAY_SOURCE_ID, way.getSource().getId());
						jsonWay.addProperty(Constants.WAY_TARGET_ID, way.getTarget().getId());

						if(!nodeIds.contains(way.getSource().getId())) {
							nodeIds.add(way.getSource().getId());

							JsonObject jsonNodeSource = new JsonObject();
							jsonNodeSource.addProperty(Constants.NODE_ID, way.getSource().getId());
							jsonNodeSource.addProperty(Constants.NODE_LATITUDE, way.getSource().getLocation().getLatitude());
							jsonNodeSource.addProperty(Constants.NODE_LONGITUDE, way.getSource().getLocation().getLongitude());
							jsonNodes.add(jsonNodeSource);
						}

						if(!nodeIds.contains(way.getTarget().getId())) {
							nodeIds.add(way.getTarget().getId());

							JsonObject jsonNodeTarget = new JsonObject();
							jsonNodeTarget.addProperty(Constants.NODE_ID, way.getTarget().getId());
							jsonNodeTarget.addProperty(Constants.NODE_LATITUDE, way.getTarget().getLocation().getLatitude());
							jsonNodeTarget.addProperty(Constants.NODE_LONGITUDE, way.getTarget().getLocation().getLongitude());
							jsonNodes.add(jsonNodeTarget);
						}
						jsonWays.add(jsonWay);
					}
					jsonWayIds.add(jsonWayId);
				}

				jsonLocation.add(Constants.PATH_WAYS_IDS, jsonWayIds);
				jsonLocations.add(jsonLocation);
			}

			jsonPath.add(Constants.PATH_LOCATIONS, jsonLocations);
			jsonPaths.add(jsonPath);
		}
		jsonPathsSet.add(Constants.PATHS, jsonPaths);
		jsonWaysSet.add(Constants.WAYS, jsonWays);
		jsonNodesSet.add(Constants.NODES, jsonNodes);

		try {
			ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(filename));

			zipOutputStream.putNextEntry(new ZipEntry(Constants.PATHS_ENTRY_NAME));
			zipOutputStream.write(jsonPathsSet.toString().getBytes(), 0, jsonPathsSet.toString().length());
			zipOutputStream.closeEntry();

			zipOutputStream.putNextEntry(new ZipEntry(Constants.WAYS_ENTRY_NAME));
			zipOutputStream.write(jsonWaysSet.toString().getBytes(), 0, jsonWaysSet.toString().length());
			zipOutputStream.closeEntry();

			zipOutputStream.putNextEntry(new ZipEntry(Constants.NODES_ENTRY_NAME));
			zipOutputStream.write(jsonNodesSet.toString().getBytes(), 0, jsonNodesSet.toString().length());
			zipOutputStream.closeEntry();

			zipOutputStream.close();

			System.out.println("Paths file created.");
		} catch (IOException e) {
			System.err.println("Error during file creation.");
			System.exit(7);
		}
	}

	private static void generatePaths() {
		try {
			System.out.println("Connecting to database…");
			Database.connect(host, db, user, password);
			System.out.println("Connected to database.");
			System.out.println("\nGenerating paths…");
			if(mode.equals(PathGeneratorMode.RANDOM)) {
				while(pathsSet.size() < numberOfPaths) {
					Path path = Database.getRandomPath(vehicle, resolution);
					boolean pathCorrectlyGenerated = true;
					while(path.length() < pathsMinimumLength) {
						Path newPath = null;
						while(newPath == null) {
							newPath = Database.getRandomPath(path.getLastLocation(), vehicle, resolution);
						}
						if(path.getLastLocation().equals(newPath.getFirstLocation())) {
							path = path.concat(newPath.subPath(newPath.indexOf(newPath.getFirstLocation()) + 1));
						} else {
							pathCorrectlyGenerated = false;
							break;
						}
					}
					if(pathCorrectlyGenerated) {
						pathsSet.add(path);
						System.out.println(pathsSet.size() + "/" + numberOfPaths + " paths generated.");
					}

				}
			} else if(mode.equals(PathGeneratorMode.SWITCHSTATION)) {
				for(Location a : switchstations) {
					for(Location b : switchstations) {
						if(!a.equals(b)) {
							Path path = Database.getPath(a, b, vehicle, resolution);
							if(path != null) {
								pathsSet.add(path);
								System.out.println(pathsSet.size() + " paths generated.");
							}
						}
					}
				}

			}
			System.out.println("Paths generated.");
			System.out.println("\nDisconnecting from database…");
			Database.disconnect();
			System.out.println("Disconnected from database.");
		} catch (ClassNotFoundException | SQLException e) {
			System.err.println("Database configuration error.");
			System.exit(6);
		}
	}

	private static void readArgs(String[] args) {
		if(args.length > 0) {
			for(int i=0; i<args.length; i++) {
				if(args[i].equals("-h")) {
					host = args[i+1];
				} else if(args[i].equals("-d")) {
					db = args[i+1];
				} else if(args[i].equals("-u")) {
					user = args[i+1];
				} else if(args[i].equals("-p")) {
					password = args[i+1];
				} else if(args[i].equals("-v")) {
					String vehicleArg = args[i+1];
					if(vehicleArg.equals("MOTORCAR")) {
						vehicle = OSMVehicle.MOTORCAR;
					} else if(vehicleArg.equals("MOTORCYCLE")) {
						vehicle = OSMVehicle.MOTORCYCLE;
					} else if(vehicleArg.equals("HORSE")) {
						vehicle = OSMVehicle.HORSE;
					} else if(vehicleArg.equals("BICYCLE")) {
						vehicle = OSMVehicle.BICYCLE;
					} else if(vehicleArg.equals("FOOT")){
						vehicle = OSMVehicle.FOOT;
					} else {
						System.err.println("Argument -v must be a valid OSMVehicle.");
						System.exit(1);
					}
				} else if(args[i].equals("-r")) {
					try {
						resolution = Double.parseDouble(args[i+1]);
					} catch (NumberFormatException e) {
						System.err.println("Argument -r must be a Double.");
						System.exit(2);
					}

				} else if(args[i].equals("-f")) {
					filename = args[i+1] + Constants.FILENAME_EXTENSION;

				} else if(args[i].equals("-m")) {
					String modeArg = args[i+1];
					if(modeArg.equals("RANDOM")) {
						mode = PathGeneratorMode.RANDOM;
						try {
							numberOfPaths = Integer.parseInt(args[i+2]);
						} catch (NumberFormatException e) {
							System.err.println("Argument -m RANDOM requires an Integer.");
							System.exit(3);
						}

					} else if(modeArg.equals("SWITCHSTATION")) {
						mode = PathGeneratorMode.SWITCHSTATION;
						switchstations = new HashSet<Location>();
						try {
							BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(new FileInputStream(new File(args[i+2]))));
							String line;

							while((line = bufferedReader.readLine()) != null) {
								String[] coordinates = line.split(",");
								switchstations.add(new Location(Double.parseDouble(coordinates[0]), Double.parseDouble(coordinates[1])));
							}

							bufferedReader.close();
						} catch (IOException e) {
							System.err.println("Argument -m SWITCHSTATION requires a valid CSV file.");
							System.exit(4);
						}

					} else {
						System.err.println("Argument -m must be a valid PathGeneratorMode.");
						System.exit(5);
					}
				} else if(args[i].equals("-l")) {
					try {
						pathsMinimumLength = Double.parseDouble(args[i+1]);
					} catch (NumberFormatException e) {
						System.err.println("Argument -l requires a Double.");
						System.exit(6);
					}
				}
			}
		} else {
			System.out.println("-h → host (the host where the database is located)");
			System.out.println("-d → db (the name of the database)");
			System.out.println("-u → user (the name of the user of the database)");
			System.out.println("-p → password (the password of the user of the database)");
			System.out.println("-v → vehicle (the type of vehicle for which the paths are generated [MOTORCAR, MOTORCYCLE, HORSE, BICYCLE, FOOT])");
			System.out.println("-r → resolution (the resolution in Kilometers between the locations of the paths generated");
			System.out.println("-f → file (the name of the file containing the paths generated)");
			System.out.println("-m → mode (the way the paths are generated [RANDOM, SWITCHSTATION])");
			System.out.println("-l → length (the minimum length of generated paths)");
		}
	}
}