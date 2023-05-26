package de.arthurpicht.barnacle.codeGenerator.sql;

import de.arthurpicht.barnacle.Const.Encoding;
import de.arthurpicht.barnacle.Const;


public class StatementGeneratorMySQL extends StatementGenerator {

	public StatementGeneratorMySQL() {
	}
	
	@Override
	public String[] deactivateForeignKeyChecks() {
		return new String[]{"SET foreign_key_checks=0;"};
	}
	
	@Override
	public String[] activateForeignKeyChecks() {
		return new String[]{"SET foreign_key_checks=1;"};
	}

	@Override
	public String addAutoIncrement(String tableName, String columnName) {
		return "ALTER TABLE " + tableName + " MODIFY COLUMN " + columnName + " INTEGER AUTO_INCREMENT;";
	}

	@Override
	public String configureEncoding(String tableName, Encoding encoding) {
		String sql = "";
		if (encoding.equals(Const.Encoding.ISO)) {
			sql = "ALTER TABLE " + tableName + " CHARACTER SET LATIN1;";
		} else if (encoding.equals(Const.Encoding.UTF)) {
			sql = "ALTER TABLE " + tableName + " CHARACTER SET UTF8;";
		}
		
		return sql;
	}

}
