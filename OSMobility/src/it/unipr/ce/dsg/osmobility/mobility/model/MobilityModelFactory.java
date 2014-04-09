package it.unipr.ce.dsg.osmobility.mobility.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * 
 * @author Giacomo Brambilla (giacomo.brambilla@studenti.unipr.it)
 * 
 */

public class MobilityModelFactory {

	private static final HashMap<String, MobilityModel> mobilityModels = new HashMap<String, MobilityModel>();

	/**
	 * Creates a MobilityModel associated with the class or interface with the given string name.
	 * The MobilityModel created is a Singleton.
	 * @param mobilityModelName
	 * - the fully qualified name of the desired MobilityModel.
	 * @return
	 * a MobilityModel associated with the class or interface with the given string name.
	 */
	public static synchronized MobilityModel createMobilityModel(String mobilityModelName) {
		if(!mobilityModels.containsKey(mobilityModelName)) {
			try {
				Class<?> mobilityModelClass = Class.forName(mobilityModelName);
				Constructor<?> mobilityModelClassConstructor;
				mobilityModelClassConstructor = mobilityModelClass.getConstructor();
				MobilityModel mobilityModel = (MobilityModel) mobilityModelClassConstructor.newInstance();
				mobilityModels.put(mobilityModelName, mobilityModel);
			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return mobilityModels.get(mobilityModelName);
	}
}
