package de.arthurpicht.barnacle.connectionManager;

public class ConnectionHelper {
	
	protected static String getJDBCConnectionString(DBConfiguration dbConfiguration) {

		String rdbms = dbConfiguration.getRdbms();
		
		if (rdbms.equals("mysql")) {
//			String jdbcConnectionString = "jdbc:mysql://" + dbConfiguration.getDbHost() + "/" + dbConfiguration.getDbName() + "?user=" + dbConfiguration.getDbUser() + "&password=" + dbConfiguration.getDbPassword() +"&dontTrackOpenResources=true";
			String jdbcConnectionString = "jdbc:mysql://" + dbConfiguration.getDbHost() 
				+ "/" + dbConfiguration.getDbName() + "?user=" + dbConfiguration.getDbUser() 
				+ "&password=" + dbConfiguration.getDbPassword() 
				+ "&dontTrackOpenResources=true"
				+ "&useSSL=false";
			
			return jdbcConnectionString;
		} 
		
		throw new RuntimeException("Configured RDBMS '" + rdbms + "' is unknown!");
	}

}
