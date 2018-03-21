package de.arthurpicht.barnacle.exceptions;

public class EntityNotFoundException extends BarnacleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 426276393454928050L;

	public EntityNotFoundException() {
		super();
	}
	
	public EntityNotFoundException(Throwable cause) {
		super(cause);
	}
	
	public EntityNotFoundException(String s) {
		super(s);
	}
	
	public EntityNotFoundException(String s, Throwable cause) {
		super(s, cause);
	}
}
