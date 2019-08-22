package de.arthurpicht.barnacle.connectionManager;


public class DBConnectionDecisionMakerSingle extends DBConnectionDecisionMaker {

	private ConnectionWrapper connectionWrapper;
	
	protected DBConnectionDecisionMakerSingle(ConnectionWrapper connectionWrapper) {
		this.connectionWrapper = connectionWrapper;
	}

	@Override
	public ConnectionWrapper getDBCoonectionByDaoClass(String canonicalClassName) {
		return this.connectionWrapper;
	}
		
}
