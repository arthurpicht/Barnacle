package de.arthurpicht.barnacle.codeGenerator.sql;

import de.arthurpicht.barnacle.Const.Encoding;

public class StatementGeneratorH2 extends StatementGenerator {

	public StatementGeneratorH2() {
	}
	
	@Override
	public String[] deactivateForeignKeyChecks() {
		return new String[]{"SET REFERENTIAL_INTEGRITY FALSE;"};
	}
	
	@Override
	public String[] activateForeignKeyChecks() {
		return new String[]{"SET REFERENTIAL_INTEGRITY TRUE;"};
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
		// from the docs: http://www.h2database.com/html/advanced.html
		// H2 internally uses Unicode, and supports all character encoding systems and character sets supported by the
		// virtual machine you use.
		return "";
	}

}
