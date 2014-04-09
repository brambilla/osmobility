package it.unipr.ce.dsg.osmobility.mobility.event;

import java.util.Properties;

import it.unipr.ce.dsg.deus.core.InvalidParamsException;
import it.unipr.ce.dsg.deus.core.Process;

/**
 * 
 * @author Giacomo Brambilla (giacomo.brambilla@studenti.unipr.it)
 *
 */

public abstract class MobileNodeDeathEvent extends GeoNodeDeathEvent {

	public MobileNodeDeathEvent(String id, Properties params, Process parentProcess) throws InvalidParamsException {
		super(id, params, parentProcess);
	}

}
