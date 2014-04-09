package it.unipr.ce.dsg.osmobility.mobility.event;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;
import it.unipr.ce.dsg.osmobility.mobility.node.MobileNode;

/**
 * 
 * @author Giacomo Brambilla (giacomo.brambilla@studenti.unipr.it)
 *
 */

public class MoveNodeEvent extends Event {

	public MoveNodeEvent(String id, Properties params, Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
	}

	@Override
	public void run() throws RunException {
		MobileNode mobileNode = (MobileNode) getAssociatedNode();		
		mobileNode.move();
	}

}
