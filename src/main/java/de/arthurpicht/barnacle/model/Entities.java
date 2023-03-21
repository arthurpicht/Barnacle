package de.arthurpicht.barnacle.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Entities {

    public static Set<ForeignKeyWrapper> getAllReferencingForeignKeys(Entity referenceEntity, EntityRelationshipModel entityRelationshipModel) {
        Set<ForeignKeyWrapper> referencingForeignKeyWrapper = new HashSet<>();
        for (Entity entity : entityRelationshipModel.getEntities()) {
            for (ForeignKeyWrapper foreignKeyWrapper : entity.getAllForeignKeys()) {
                if (foreignKeyWrapper.getTargetEntity().getTableName().equals(referenceEntity.getTableName())) {
                    referencingForeignKeyWrapper.add(foreignKeyWrapper);
                }
            }
        }
        return referencingForeignKeyWrapper;
    }

}
