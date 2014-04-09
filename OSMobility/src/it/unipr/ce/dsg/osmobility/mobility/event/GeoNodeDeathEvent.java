package it.unipr.ce.dsg.osmobility.mobility.event;

import java.util.HashSet;
import java.util.Properties;

import it.unipr.ce.dsg.deus.core.Engine;
import it.unipr.ce.dsg.deus.core.Event;
import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Process;
import it.unipr.ce.dsg.deus.core.RunException;

/**
 * 
 * @author Giacomo Brambilla (giacomo.brambilla@studenti.unipr.it)
 * 
 */

public class GeoNodeDeathEvent extends Event {

	public GeoNodeDeathEvent(String id, Properties params, Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
	}

	@Override
	public void run() throws RunException {
		HashSet<Event> events = new HashSet<Event>();
		for(Event event : Engine.getDefault().getEventsList()) {
			if((event.getAssociatedNode() != null) && event.getAssociatedNode().equals(associatedNode)) {
				events.add(event);
			}
		}
		Engine.getDefault().getEventsList().removeAll(events);
		Engine.getDefault().removeNode(associatedNode);
	}

}
