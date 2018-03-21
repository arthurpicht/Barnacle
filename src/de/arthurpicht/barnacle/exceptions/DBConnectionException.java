package de.arthurpicht.barnacle.exceptions;

public class DBConnectionException extends BarnacleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 426276393454928050L;

	public DBConnectionException() {
		super();
	}
	
	public DBConnectionException(Throwable cause) {
		super(cause);
	}
	
	public DBConnectionException(String s) {
		super(s);
	}
	
	public DBConnectionException(String s, Throwable cause) {
		super(s, cause);
	}
}
