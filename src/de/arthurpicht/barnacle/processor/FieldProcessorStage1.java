package de.arthurpicht.barnacle.processor;

import java.lang.reflect.Field;

import de.arthurpicht.barnacle.BarnacleInitializer;
import de.arthurpicht.barnacle.annotations.Annotations.Barnacle;
import de.arthurpicht.barnacle.annotations.Annotations.ColumnName;
import de.arthurpicht.barnacle.annotations.Annotations.Default;
import de.arthurpicht.barnacle.annotations.Annotations.NotNull;
import de.arthurpicht.barnacle.annotations.Annotations.PrimaryKey;
import de.arthurpicht.barnacle.annotations.Annotations.Type;
import de.arthurpicht.barnacle.annotations.Annotations.Unique;
import de.arthurpicht.barnacle.exceptions.GeneratorException;
import de.arthurpicht.barnacle.exceptions.UnknownTypeException;
import de.arthurpicht.barnacle.generator.sql.TypeMapper;
import de.arthurpicht.barnacle.mapping.Attribute;
import de.arthurpicht.barnacle.mapping.Entity;


@SuppressWarnings("rawtypes")
public class FieldProcessorStage1 {
	
	private static TypeMapper typeMapper = TypeMapper.getInstance(BarnacleInitializer.getDatabase());
	
	public static void process(Class<?> vofClass, Entity entity) throws GeneratorException {
		
		Field[] fields = vofClass.getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(Barnacle.class)) {
				processField(field, entity);				
			}
		}
	}
	
	/**
	 * Verarbeitet ein Barnacle-annotiertes Feld zu einem Attribut.
	 * 
	 * @param field
	 * @param entity
	 */
	private static void processField(Field field, Entity entity) throws GeneratorException {
	
		// Create new attribute object and append to entity
		Attribute attribute = new Attribute(field, entity);
		entity.addAttribute(attribute);

//		// determine fieldname
//		String fieldName = field.getName();
//		attribute.setFieldName(fieldName);
//
//		// determine columnname
//		// set as fieldname if not given
//		String columnName = new String();
//		if (field.isAnnotationPresent(ColumnName.class)) {
//			ColumnName columnNameAnnotation = (ColumnName) field.getAnnotation(ColumnName.class);
//			columnName = columnNameAnnotation.value();
//		} else {
//			columnName = fieldName;
//		}
//		attribute.setColumnName(columnName);
//
//		// determine field type
//		Class fieldTypeClass = field.getType();
//		String fieldType = fieldTypeClass.getSimpleName();
//		attribute.setFieldType(fieldType);
//
//		// determine PK
//		boolean isPrimaryKey = false;
//		boolean isAutoIncrement = false;
//		if (field.isAnnotationPresent(PrimaryKey.class)) {
//			isPrimaryKey = true;
//			PrimaryKey primaryKey = (PrimaryKey) field.getAnnotation(PrimaryKey.class);
//			isAutoIncrement = primaryKey.autoIncrement();
//		}
//		attribute.setPrimaryKey(isPrimaryKey);
//		attribute.setAutoIncrement(isAutoIncrement);
//
//		// determine 'not null' flag
//		boolean notNull = false;
//		if (field.isAnnotationPresent(NotNull.class)) {
//			notNull = true;
//		}
//		attribute.setNotNull(notNull);
//
//		// determine default value
//		String defaultValue = new String();
//		if (field.isAnnotationPresent(Default.class)) {
//			Default defaultAnnotation = (Default) field.getAnnotation(Default.class);
//			defaultValue = defaultAnnotation.value();
//			attribute.setDefaultValue(defaultValue);
//		} else {
//			attribute.setDefaultValue(null);
//		}
//
//		// determine custom type
//		String type = null;
//		Integer para1 = null;
//		Integer para2 = null;
//		if (field.isAnnotationPresent(Type.class)) {
//			Type typeAnnotation = (Type) field.getAnnotation(Type.class);
//			type = typeAnnotation.type();
//			String para1String = typeAnnotation.para1();
//			if (!para1String.equals("")) {
//				para1 = new Integer(para1String);
//
//				String para2String = typeAnnotation.para2();
//				if (!para2String.equals("")) {
//					para2 = new Integer(para2String);
//				}
//			}
//		}
//		attribute.setType(type);
//		attribute.setPara1(para1);
//		attribute.setPara2(para2);
//
//		// unique
//		if (field.isAnnotationPresent(Unique.class)) {
//			Unique uniqueAnnotation = (Unique) field.getAnnotation(Unique.class);
//			String indexName = uniqueAnnotation.name();
//			if (indexName.equals("")) {
//				entity.addUniqueField("uk_" + attribute.getColumnName(), attribute);
//			} else {
//				entity.addUniqueField(indexName, attribute);
//			}
//		}
//
//		// Determine SQL datatype by using database specific TypeMapper
//		String sqlDataType;
//		try {
//			sqlDataType = typeMapper.getSQLType(attribute);
//		} catch (UnknownTypeException e) {
//			throw new GeneratorException(e);
//		}
//		attribute.setSqlDataType(sqlDataType);

	}

}
