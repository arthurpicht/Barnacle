package de.arthurpicht.barnacle.exceptions;

public class EntityAlreadyExistsException extends BarnacleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 426276393454928050L;

	public EntityAlreadyExistsException() {
		super();
	}
	
	public EntityAlreadyExistsException(Throwable cause) {
		super(cause);
	}
	
	public EntityAlreadyExistsException(String s) {
		super(s);
	}
	
	public EntityAlreadyExistsException(String s, Throwable cause) {
		super(s, cause);
	}
}
