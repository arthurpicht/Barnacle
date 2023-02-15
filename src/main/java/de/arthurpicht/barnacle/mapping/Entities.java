package de.arthurpicht.barnacle.mapping;

import java.util.HashSet;
import java.util.Set;

public class Entities {

    public static Set<ForeignKeyWrapper> getAllReferencingForeignKeys(Entity referenceEntity, EntityCollection entityCollection) {
        Set<ForeignKeyWrapper> referencingForeignKeyWrapper = new HashSet<>();
        for (Entity entity : entityCollection.getEntities()) {
            for (ForeignKeyWrapper foreignKeyWrapper : entity.getAllForeignKeys()) {
                if (foreignKeyWrapper.getTargetEntity().getTableName().equals(referenceEntity.getTableName())) {
                    referencingForeignKeyWrapper.add(foreignKeyWrapper);
                }
            }
        }
        return referencingForeignKeyWrapper;
    }

}
