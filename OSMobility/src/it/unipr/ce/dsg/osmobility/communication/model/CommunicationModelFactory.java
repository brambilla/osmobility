package it.unipr.ce.dsg.osmobility.communication.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * 
 * @author Giacomo Brambilla (giacomo.brambilla@studenti.unipr.it)
 * 
 */

public class CommunicationModelFactory {
	
	private static final HashMap<String, CommunicationModel> communicationModels = new HashMap<String, CommunicationModel>();
	
	/**
	 * Creates a {@link CommunicationModel} associated with the class or interface with the given string name.
	 * The {@link CommunicationModel} created is a Singleton.
	 * @param communicationModelName
	 * - the fully qualified name of the desired {@link CommunicationModel}.
	 * @return
	 * a {@link CommunicationModel} associated with the class or interface with the given string name.
	 */
	public static synchronized CommunicationModel createCommunicationModel(String communicationModelName) {
		if(!communicationModels.containsKey(communicationModelName)) {
			try {
				Class<?> communicationModelClass = Class.forName(communicationModelName);
				Constructor<?> communicationModelClassConstructor;
				communicationModelClassConstructor = communicationModelClass.getConstructor();
				CommunicationModel communicationModel = (CommunicationModel) communicationModelClassConstructor.newInstance();
				communicationModels.put(communicationModelName, communicationModel);
			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return communicationModels.get(communicationModelName);
	}

}
