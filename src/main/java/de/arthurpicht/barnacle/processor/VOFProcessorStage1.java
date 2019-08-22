package de.arthurpicht.barnacle.processor;

import de.arthurpicht.barnacle.annotations.Annotations.TableName;
import de.arthurpicht.barnacle.annotations.Annotations.VobFactory;
import de.arthurpicht.barnacle.exceptions.GeneratorException;
import de.arthurpicht.barnacle.mapping.Entity;
import de.arthurpicht.barnacle.mapping.EntityCollection;

/**
 * Erzeugt aus übergebener VO-Klasse eine Stage1-Tabellen-
 * Repräsentation einschließlich Attributen, PK aber ohne
 * FK-Constraint.
 * 
 * @author Arthur Picht, Arthur Picht GmbH, (c) 2007
 *
 */
public class VOFProcessorStage1 {
	
	/**
	 * Startet die Stage1-Verarbeitung der übergebenen
	 * VOF-Klasse. Voraussetzung: Klasse besitzt die Barnacle-
	 * Annotation.
	 * 
	 * @param canonicalClassName
	 */
	public static void process(Class<?> vofClass) throws GeneratorException {
		
		// Neuen Table erzeugen und der TableCollection
		// hinzufügen.
		Entity entity = new Entity(vofClass);
		EntityCollection.addEntity(entity);
		
		// Table-Name festlegen: Entweder durch @TableName definiert
		// oder als SimpleName der Klasse abzuüglich der Endung 'VOF'.
		if (vofClass.isAnnotationPresent(TableName.class)) {
			
			TableName tableName = (TableName) vofClass.getAnnotation(TableName.class);
			String tableNameString = tableName.value();
			
			entity.setTableName(tableNameString);
			
		} else {
			String vofSimpleClassName = vofClass.getSimpleName();
			String tableNameString = vofSimpleClassName.substring(0, vofSimpleClassName.length() - 3);
			tableNameString = tableNameString.toLowerCase();
			entity.setTableName(tableNameString);
		}
		
		// Check whether vo should contain vob factory.
		if (vofClass.isAnnotationPresent(VobFactory.class)) {
			entity.setVobFactoryMethod(true);
		}
		
		// Übergeben an Field-Prozessor
		FieldProcessorStage1.process(vofClass, entity);
	}

}
