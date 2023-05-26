package de.arthurpicht.barnacle.codeGenerator.sql;

import de.arthurpicht.barnacle.exceptions.BarnacleRuntimeException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SqlScriptWriter {

	public static void write(Path scriptFile, SqlStatements sqlStatements) {
		try {
			Files.write(scriptFile, sqlStatements.getSqlStatementList());
		} catch (IOException e) {
			throw new BarnacleRuntimeException("Could not write script file [" + scriptFile.toAbsolutePath() + "]. " +
					"Cause: " + e.getMessage(), e);
		}
	}

}

