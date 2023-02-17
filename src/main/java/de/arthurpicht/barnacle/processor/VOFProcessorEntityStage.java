package de.arthurpicht.barnacle.processor;

import de.arthurpicht.barnacle.annotations.Annotations;
import de.arthurpicht.barnacle.annotations.Annotations.TableName;
import de.arthurpicht.barnacle.annotations.Annotations.VobFactory;
import de.arthurpicht.barnacle.configuration.GeneratorConfiguration;
import de.arthurpicht.barnacle.model.Attribute;
import de.arthurpicht.barnacle.model.ERMBuilderException;
import de.arthurpicht.barnacle.model.Entity;

import java.lang.reflect.Field;

/**
 * Erzeugt aus übergebener VO-Klasse eine Entity/Tabellen-
 * Repräsentation, einschließlich Attributen, PK aber ohne
 * FK-Constraint.
 * 
 * @author Arthur Picht (c) 2007 - 2023
 */
public class VOFProcessorEntityStage {
	
	public static Entity process(Class<?> vofClass, GeneratorConfiguration generatorConfiguration)
			throws ERMBuilderException {

		Entity entity = new Entity(vofClass, generatorConfiguration);

		String tableNameString = obtainTableName(vofClass);
		entity.setTableName(tableNameString);

		if (vofClass.isAnnotationPresent(VobFactory.class)) {
			entity.setVobFactoryMethod(true);
		}
		
		analyzeFields(vofClass, entity);

		return entity;
	}

	private static String obtainTableName(Class<?> vofClass) {
		if (vofClass.isAnnotationPresent(TableName.class)) {
			return getAnnotatedTableName(vofClass);
		} else {
			return getDefaultTableName(vofClass);
		}
	}

	private static String getAnnotatedTableName(Class<?> vofClass) {
		TableName tableName = vofClass.getAnnotation(TableName.class);
		return tableName.value();
	}

	private static String getDefaultTableName(Class<?> vofClass) {
		String vofSimpleClassName = vofClass.getSimpleName();
		String tableNameString = vofSimpleClassName.substring(0, vofSimpleClassName.length() - 3);
		return tableNameString.toLowerCase();
	}

	private static void analyzeFields(Class<?> vofClass, Entity entity) {
		Field[] fields = vofClass.getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(Annotations.Barnacle.class)) {
				processField(field, entity);
			}
		}
	}

	private static void processField(Field field, Entity entity) {
		Attribute attribute = new Attribute(field, entity);
		entity.addAttribute(attribute);
	}

}
