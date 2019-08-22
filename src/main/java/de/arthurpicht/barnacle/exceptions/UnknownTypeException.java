package de.arthurpicht.barnacle.exceptions;

public class UnknownTypeException extends BarnacleException {


	/**
	 * 
	 */
	private static final long serialVersionUID = 7286506474703547393L;

	public UnknownTypeException() {
		super();
	}
	
	public UnknownTypeException(Throwable cause) {
		super(cause);
	}
	
	public UnknownTypeException(String s) {
		super(s);
	}
	
	public UnknownTypeException(String s, Throwable cause) {
		super(s, cause);
	}
}
