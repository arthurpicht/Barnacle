package de.arthurpicht.barnacle;

import de.arthurpicht.barnacle.exceptions.BarnacleRuntimeException;
import de.arthurpicht.barnacle.generator.BarnacleGenerator;

// TODO Replace with user friendly ClI
public class BarnacleGeneratorStarter {

	public static void main(String[] args) {
		try {
			BarnacleGenerator.process();
		} catch (BarnacleRuntimeException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
}
