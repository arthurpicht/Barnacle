package de.arthurpicht.barnacle.processor;

import de.arthurpicht.barnacle.exceptions.BarnacleInitializerException;
import de.arthurpicht.barnacle.mapping.Attribute;
import de.arthurpicht.barnacle.mapping.Entity;
import de.arthurpicht.barnacle.mapping.EntityCollection;
import de.arthurpicht.utils.core.strings.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EntityStageValidator {

    public static void validate(EntityCollection entityCollection) {
        for (Entity entity : entityCollection.getEntities()) {
            List<Attribute> autoIncrementAttributes = selectAutoIncrementAttributes(entity);
            assertMaxOneAutoIncrementField(autoIncrementAttributes, entity);
            assertCorrectType(autoIncrementAttributes, entity);
        }
    }

    private static List<Attribute> selectAutoIncrementAttributes(Entity entity) {
        List<Attribute> autoIncrementAttributes = new ArrayList<>();
        List<Attribute> attributes = entity.getAttributes();
        for (Attribute attribute : attributes) {
            if (attribute.isAutoIncrement())
                autoIncrementAttributes.add(attribute);
        }
        return autoIncrementAttributes;
    }

    private static void assertMaxOneAutoIncrementField(List<Attribute> autoIncrementAttributes, Entity entity) {
        if (autoIncrementAttributes.size() > 1) {
            List<String> fieldNames = autoIncrementAttributes.stream()
                    .map(Attribute::getFieldName)
                    .collect(Collectors.toList());
            String fieldNameListing = Strings.listing(fieldNames, "", "", "", "[", "]");

            throw new BarnacleInitializerException(
                    "More than one autoIncrement field found in VOF file [" + entity.getVofSimpleClassName() + "]: "
                            + fieldNameListing + ".");
        }
    }

    private static void assertCorrectType(List<Attribute> autoIncrementAttributes, Entity entity) {
        if (autoIncrementAttributes.size() == 1) {
            Attribute attribute = autoIncrementAttributes.get(0);
            String javaTypeSimpleName = attribute.getJavaTypeSimpleName();
            if (!(javaTypeSimpleName.equals("int") || (javaTypeSimpleName.equals("Integer"))))
                throw new BarnacleInitializerException(
                        "autoIncrement field [" + entity.getVofSimpleClassName() + "." + attribute.getFieldName() + "] "
                                + " is not of type int or Integer."
                );
        }
    }

}
