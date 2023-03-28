package de.arthurpicht.barnacle.connectionManager;


public class DBConnectionDecisionMakerSingle extends DBConnectionDecisionMaker {

	private final ConnectionWrapper connectionWrapper;
	
	protected DBConnectionDecisionMakerSingle(ConnectionWrapper connectionWrapper) {
		this.connectionWrapper = connectionWrapper;
	}

	@Override
	public ConnectionWrapper getDBConnectionByDaoClass(String canonicalClassName) {
		return this.connectionWrapper;
	}
		
}
