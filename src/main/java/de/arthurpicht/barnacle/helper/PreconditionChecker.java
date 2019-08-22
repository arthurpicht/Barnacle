package de.arthurpicht.barnacle.helper;

import de.arthurpicht.barnacle.configuration.GeneratorConfiguration;
import de.arthurpicht.barnacle.context.GeneratorContext;
import de.arthurpicht.barnacle.exceptions.BarnacleInititalizerException;

import java.io.File;

public class PreconditionChecker {
	
	public static void check() throws BarnacleInititalizerException {

        GeneratorConfiguration generatorConfiguration = GeneratorContext.getInstance().getGeneratorConfiguration();
		
		// Check whether src-dir exists
		File sourceFolder = new File(generatorConfiguration.getSrcDir());
		if (!sourceFolder.exists()) {
			throw new BarnacleInititalizerException("Source folder '" + generatorConfiguration.getSrcDir() + "' does not exist.");
		}
		if (!sourceFolder.isDirectory()) {
			throw new BarnacleInititalizerException("Configured source folder '" + generatorConfiguration.getSrcDir() + "' does not appear to be a directory.");
		}
		
		// VOF package
		String vofPackageName = generatorConfiguration.getVofPackageName();
		String vofPath = vofPackageName.replace('.', '/');
		File vofFolder = new File(sourceFolder, vofPath);
		if (!vofFolder.exists() || !vofFolder.isDirectory()) {
			throw new BarnacleInititalizerException("Folder '" + vofFolder.getAbsolutePath() + "' representing VOF package '" + generatorConfiguration.getVofPackageName() + "' does not exist.");
		}

		// VOB package
		String vobPackageName = generatorConfiguration.getVobPackageName();
		String vobPath = vobPackageName.replace('.', '/');
		File vobFolder = new File(sourceFolder, vobPath);
		if (!vobFolder.exists() || !vobFolder.isDirectory()) {
			throw new BarnacleInititalizerException("Folder '" + vobFolder.getAbsolutePath() + "' representing VOB package '" + generatorConfiguration.getVobPackageName() + "' does not exist.");
		}
		
		// src-gen
		File sourceGenFolder = new File(generatorConfiguration.getSrcGenDir());
		if (!sourceGenFolder.exists()) {
			throw new BarnacleInititalizerException("Source generation folder '" + generatorConfiguration.getSrcGenDir() + "' does not exist.");
		}
		if (!sourceGenFolder.isDirectory()) {
			throw new BarnacleInititalizerException("Configured source generation folder '" + generatorConfiguration.getSrcGenDir() + "' does not appear to be a directory.");
		}
		
		// VO package:
		// create if not pre-existing
		String voPackageName = generatorConfiguration.getVoPackageName();
		String voPath = voPackageName.replace('.', '/');
		File voFolder = new File(sourceGenFolder, voPath);
		if (!voFolder.exists()) {
			boolean success = voFolder.mkdirs();
			if (!success) throw new BarnacleInititalizerException("Could not create directory for VO package: " + voFolder.getAbsolutePath());
		}

		// DAO package:
		// create if not pre-existing
		String daoPackageName = generatorConfiguration.getDaoPackageName();
		String daoPath = daoPackageName.replace('.', '/');
		File daoFolder = new File(sourceGenFolder, daoPath);
		if (!daoFolder.exists()) {
			boolean success = daoFolder.mkdirs();
			if (!success) throw new BarnacleInititalizerException("Could not create directory for DAO package: " + daoFolder.getAbsolutePath());
		}

		// script file
		// is configured file contains a path declaration, check whether parent directory exists.
		if (generatorConfiguration.isCreateScript()) {
			String scriptFileName = generatorConfiguration.getScriptFile();
			File scriptFile = new File(scriptFileName);
			File parent = scriptFile.getParentFile();
			if (parent != null) {
				if (!parent.exists()) {
					throw new BarnacleInititalizerException("Parent directory of configured script file does not exist: " + parent.getAbsolutePath());
				}
			}
		}
		
		
	}

}
