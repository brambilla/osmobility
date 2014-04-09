package it.unipr.ce.dsg.osmobility.communication.model;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.osmobility.communication.Message;
import it.unipr.ce.dsg.osmobility.mobility.node.GeoNode;

/**
 * 
 * @author Giacomo Brambilla (giacomo.brambilla@studenti.unipr.it)
 *
 */

public class DelayModel extends CommunicationModel {

	private double uplink = 200.0;
	private double downlink = 2000.0;

	@Override
	public Double evaluateSend(Message message, GeoNode geoNode) {
		double senderUplink = this.expRandom(uplink);
		double receiverDownlink = this.expRandom(downlink);
		double speed = Math.min(senderUplink, receiverDownlink);

		double kiloBits = message.getBytesLength()/125.0;

		return (kiloBits / speed)*1000.0;
	}
	
	private double expRandom(double meanValue) {
		double random = (double) (-Math.log(Engine.getDefault().getSimulationRandom().nextDouble()) * meanValue);
		return random;
	}

	@Override
	public void initialize(String[] params) {
		if(params.length == 2) {
			uplink = Double.valueOf(params[0]);
			downlink = Double.valueOf(params[1]);
		}
	}

}
