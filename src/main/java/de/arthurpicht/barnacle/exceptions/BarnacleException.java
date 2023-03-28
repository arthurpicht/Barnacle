package de.arthurpicht.barnacle.exceptions;

public class BarnacleException extends Exception {

	private static final long serialVersionUID = 426276393454928050L;

	public BarnacleException() {
		super();
	}
	
	public BarnacleException(Throwable cause) {
		super(cause);
	}
	
	public BarnacleException(String s) {
		super(s);
	}
	
	public BarnacleException(String s, Throwable cause) {
		super(s, cause);
	}

}
