package de.arthurpicht.barnacle.exceptions;

public class BarnacleInitializerException extends BarnacleRuntimeException {

	private static final long serialVersionUID = -1374885303554361297L;

	public BarnacleInitializerException() {
		super();
	}
	
	public BarnacleInitializerException(Throwable cause) {
		super(cause);
	}
	
	public BarnacleInitializerException(String s) {
		super(s);
	}
	
	public BarnacleInitializerException(String s, Throwable cause) {
		super(s, cause);
	}
}
