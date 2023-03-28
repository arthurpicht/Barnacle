package de.arthurpicht.barnacle.processor;

import de.arthurpicht.barnacle.model.Entity;
import de.arthurpicht.barnacle.model.EntityRelationshipModel;

public class VOFProcessorStage2 {
	
	public static void process(Entity entity, EntityRelationshipModel entityRelationshipModel) {
		FieldProcessorStage2.process(entity, entityRelationshipModel);
	}

}
