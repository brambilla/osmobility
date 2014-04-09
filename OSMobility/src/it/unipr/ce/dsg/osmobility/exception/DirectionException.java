package it.unipr.ce.dsg.osmobility.exception;

/**
 * 
 * @author Giacomo Brambilla (giacomo.brambilla@studenti.unipr.it)
 *
 */

public class DirectionException extends Exception {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructs a new direction exception with the specified detail message. The cause is not initialized, and may subsequently be initialized by a call to Throwable.initCause(java.lang.Throwable).
	 * @param message
	 * - the detail message. The detail message is saved for later retrieval by the Throwable.getMessage() method.
	 */
	public DirectionException(String message) {
		super(message);
	}

}
