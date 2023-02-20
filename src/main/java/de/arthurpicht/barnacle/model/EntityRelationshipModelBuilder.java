package de.arthurpicht.barnacle.model;

import de.arthurpicht.barnacle.annotations.Annotations;
import de.arthurpicht.barnacle.configuration.generator.GeneratorConfiguration;
import de.arthurpicht.barnacle.processor.*;

import java.util.List;

public class EntityRelationshipModelBuilder {

    public static EntityRelationshipModel execute(
            GeneratorConfiguration generatorConfiguration,
            List<Class<?>> classes) {

        EntityRelationshipModel entityRelationshipModel = new EntityRelationshipModel();

        // Step 1: Build entity and attribute representations from all VOF-files
        for (Class<?> clazz : classes) {
            if (clazz.getSimpleName().endsWith("VOF") && clazz.isAnnotationPresent(Annotations.Barnacle.class)) {
                Entity entity = VOFProcessorEntityStage.process(clazz, generatorConfiguration);
                entityRelationshipModel.addEntity(entity);
            }
        }

        EntityStageValidator.validate(entityRelationshipModel);

        // Step 2: Processing VOF-Files again: Stage2.
        // Analyze relations.
        for (Entity entity : entityRelationshipModel.getEntities()) {
            FieldProcessorStage2.process(entity, entityRelationshipModel);
        }

        // Association-Tables
        for (Entity entity : entityRelationshipModel.getEntities()) {
            VOFProcessorStage3.process(entity);
        }

        // Step 3: Validating entities and relations
        for (Entity entity : entityRelationshipModel.getEntities()) {
            EntityValidator.validate(entity);
        }
        for (Entity entity : entityRelationshipModel.getEntities()) {
            RelationValidator.validate(entity);
        }

        return entityRelationshipModel;
    }

}
