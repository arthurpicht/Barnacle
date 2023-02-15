package de.arthurpicht.barnacle.processor;

import de.arthurpicht.barnacle.exceptions.GeneratorException;
import de.arthurpicht.barnacle.mapping.Entity;
import de.arthurpicht.barnacle.mapping.EntityCollection;

public class VOFProcessorStage2 {
	
	public static void process(Entity entity, EntityCollection entityCollection) throws GeneratorException {
		
		FieldProcessorStage2.process(entity, entityCollection);
	}

}
