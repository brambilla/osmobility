package it.unipr.ce.dsg.osmobility.mobility.event;

import java.util.Properties;
import java.util.TreeMap;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Node;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.osmobility.communication.model.CommunicationModel;
import it.unipr.ce.dsg.osmobility.communication.model.CommunicationModelFactory;
import it.unipr.ce.dsg.osmobility.mobility.node.GeoNode;

/**
 * 
 * @author Giacomo Brambilla (giacomo.brambilla@studenti.unipr.it)
 * 
 */

public abstract class GeoNodeBirthEvent extends Event {

	private CommunicationModel communicationModel;

	public GeoNodeBirthEvent(String id, Properties params, Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);

		TreeMap<Integer, String> communicationModelParams = new TreeMap<Integer, String>();
		for(String propertyName : params.stringPropertyNames()) {
			if(propertyName.contains("communicationModel#")) {
				Integer paramIndex = Integer.valueOf(propertyName.substring(propertyName.indexOf("#param") + "#param".length()));
				communicationModelParams.put(paramIndex, params.getProperty(propertyName));
			}
		}

		String communicationModelParam = params.getProperty("communicationModel");
		communicationModel = CommunicationModelFactory.createCommunicationModel(communicationModelParam);
		if(!communicationModelParams.isEmpty()) {
			communicationModel.initialize(communicationModelParams.values().toArray(new String[communicationModelParams.size()]));
		}
	}

	@Override
	public void run() throws RunException {
		Node node = (Node) getParentProcess().getReferencedNodes().get(Engine.getDefault().getSimulationRandom().nextInt(getParentProcess().getReferencedNodes().size())).createInstance(Engine.getDefault().generateKey());
		GeoNode geoNode = (GeoNode) node;
		Engine.getDefault().addNode(geoNode);
		geoNode.initCommunication(communicationModel);
		associatedNode = geoNode;
	}

}
