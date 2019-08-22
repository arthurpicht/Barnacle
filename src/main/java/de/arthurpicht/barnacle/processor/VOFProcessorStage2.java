package de.arthurpicht.barnacle.processor;

import de.arthurpicht.barnacle.exceptions.GeneratorException;
import de.arthurpicht.barnacle.mapping.Entity;

public class VOFProcessorStage2 {
	
	public static void process(Entity entity) throws GeneratorException {
		
		FieldProcessorStage2.process(entity);
	}

}
