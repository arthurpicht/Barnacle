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
	public String addAutoIncrement(String tableName, String columnName) {
		return "ALTER TABLE " + tableName + " ALTER COLUMN " + columnName + " INTEGER AUTO_INCREMENT;";
	}

	@Override
	public String configureEncoding(String tableName, Encoding encoding) {
		// from the docs: http://www.h2database.com/html/advanced.html
		// H2 internally uses Unicode, and supports all character encoding systems and character sets supported by the
		// virtual machine you use.
		return "";
	}

}
