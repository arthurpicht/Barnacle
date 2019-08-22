package de.arthurpicht.barnacle.exceptions;

public class InconsistentEntityException extends BarnacleException {


	/**
	 * 
	 */
	private static final long serialVersionUID = 7067303585198800672L;

	public InconsistentEntityException() {
		super();
	}
	
	public InconsistentEntityException(Throwable cause) {
		super(cause);
	}
	
	public InconsistentEntityException(String s) {
		super(s);
	}
	
	public InconsistentEntityException(String s, Throwable cause) {
		super(s, cause);
	}
}
