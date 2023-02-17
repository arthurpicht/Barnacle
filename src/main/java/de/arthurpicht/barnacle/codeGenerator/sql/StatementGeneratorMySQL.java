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
	public String addAutoIncrement(String tablename, String columnName, String sqlType, String defaultValue, boolean notNull) {
		String sql = "ALTER TABLE " + tablename + " MODIFY COLUMN " + columnName + " " + sqlType;
		if (defaultValue != null) {
			sql += " DEFAULT '" + defaultValue + "'";
		}
		if (notNull) {
			sql += " NOT NULL";
		}
		sql += " AUTO_INCREMENT;";		
		
		return sql;
	}

	@Override
	public String configureEncoding(String tablename, Encoding encoding) {
		String sql = "";
		if (encoding.equals(Const.Encoding.ISO)) {
			sql = "ALTER TABLE " + tablename + " CHARACTER SET LATIN1;";
		} else if (encoding.equals(Const.Encoding.UTF)) {
			sql = "ALTER TABLE " + tablename + " CHARACTER SET UTF8;";
		}
		
		return sql;
	}

}
