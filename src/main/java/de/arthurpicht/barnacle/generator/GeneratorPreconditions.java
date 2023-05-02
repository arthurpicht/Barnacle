package de.arthurpicht.barnacle.generator;

import de.arthurpicht.barnacle.configuration.generator.GeneratorConfiguration;

import java.io.File;

public class GeneratorPreconditions {
	
	public static void assure(GeneratorConfiguration generatorConfiguration) {
		File srcFolder = assertSrcDir(generatorConfiguration);
		assertVofFolder(srcFolder, generatorConfiguration);
		assertVobFolder(srcFolder, generatorConfiguration);
		File srcGenFolder = assertSrcGenFolder(generatorConfiguration);
		assureVoFolder(srcGenFolder, generatorConfiguration);
		assureDaoFolder(srcGenFolder, generatorConfiguration);
		assertScriptParentFolder(generatorConfiguration);
	}

	private static File assertSrcDir(GeneratorConfiguration generatorConfiguration) {
		File sourceFolder = new File(generatorConfiguration.getSrcDir());
		if (!sourceFolder.exists()) {
			throw new GeneratorPreconditionException(
					"Source folder [" + generatorConfiguration.getSrcDir() + "] does not exist.");
		}
		if (!sourceFolder.isDirectory()) {
			throw new GeneratorPreconditionException(
					"Configured source folder [" + generatorConfiguration.getSrcDir()
							+ "] does not appear to be a directory.");
		}
		return sourceFolder;
	}

	private static void assertVofFolder(File sourceFolder, GeneratorConfiguration generatorConfiguration) {
		String vofPackageName = generatorConfiguration.getVofPackageName();
		String vofPath = vofPackageName.replace('.', '/');
		File vofFolder = new File(sourceFolder, vofPath);
		if (!vofFolder.exists() || !vofFolder.isDirectory()) {
			throw new GeneratorPreconditionException(
					"Folder [" + vofFolder.getAbsolutePath()
							+ "] representing VOF package [" + generatorConfiguration.getVofPackageName()
							+ "] does not exist.");
		}
	}

	private static void assertVobFolder(File sourceFolder, GeneratorConfiguration generatorConfiguration) {
		String vobPackageName = generatorConfiguration.getVobPackageName();
		String vobPath = vobPackageName.replace('.', '/');
		File vobFolder = new File(sourceFolder, vobPath);
		if (!vobFolder.exists() || !vobFolder.isDirectory()) {
			throw new GeneratorPreconditionException(
					"Folder [" + vobFolder.getAbsolutePath()
							+ "] representing VOB package [" + generatorConfiguration.getVobPackageName()
							+ "] does not exist.");
		}
	}

	private static File assertSrcGenFolder(GeneratorConfiguration generatorConfiguration) {
		File sourceGenFolder = new File(generatorConfiguration.getSrcGenDir());
		if (!sourceGenFolder.exists()) {
			throw new GeneratorPreconditionException(
					"Source generation folder [" + generatorConfiguration.getSrcGenDir() + "] does not exist.");
		}
		if (!sourceGenFolder.isDirectory()) {
			throw new GeneratorPreconditionException(
					"Configured source generation folder [" + generatorConfiguration.getSrcGenDir()
							+ "] does not appear to be a directory.");
		}
		return sourceGenFolder;
	}

	private static void assureVoFolder(File sourceGenFolder, GeneratorConfiguration generatorConfiguration) {
		String voPackageName = generatorConfiguration.getVoPackageName();
		String voPath = voPackageName.replace('.', '/');
		File voFolder = new File(sourceGenFolder, voPath);
		if (!voFolder.exists()) {
			boolean success = voFolder.mkdirs();
			if (!success) throw new GeneratorPreconditionException(
					"Could not create directory for VO package: [" + voFolder.getAbsolutePath() + "].");
		}
	}

	private static void assureDaoFolder(File sourceGenFolder, GeneratorConfiguration generatorConfiguration) {
		String daoPackageName = generatorConfiguration.getDaoPackageName();
		String daoPath = daoPackageName.replace('.', '/');
		File daoFolder = new File(sourceGenFolder, daoPath);
		if (!daoFolder.exists()) {
			boolean success = daoFolder.mkdirs();
			if (!success) throw new GeneratorPreconditionException(
					"Could not create directory for DAO package: " + daoFolder.getAbsolutePath() + "].");
		}
	}

	private static void assertScriptParentFolder(GeneratorConfiguration generatorConfiguration) {
		if (generatorConfiguration.isCreateScript()) {
			String scriptFileName = generatorConfiguration.getScriptFile();
			File scriptFile = new File(scriptFileName);
			File parent = scriptFile.getParentFile();
			if (parent != null) {
				if (!parent.exists()) {
					throw new GeneratorPreconditionException(
							"Parent directory of configured script file does not exist: "
									+ parent.getAbsolutePath() + "].");
				}
			}
		}
	}

}
