package de.arthurpicht.barnacle.processor;

import de.arthurpicht.barnacle.exceptions.GeneratorException;
import de.arthurpicht.barnacle.mapping.Attribute;
import de.arthurpicht.barnacle.mapping.Entity;

import java.util.List;

public class EntityValidator {
	
	/**
	 * Proceeds some validation tasks on entities.
	 * 
	 * @param entity
	 * @throws GeneratorException
	 */
	public static void validate(Entity entity) throws GeneratorException {
				
		checkSpecConstraintPK_1(entity);
		checkSpecConstraintPK_AI_1(entity);
		checkSpecConstraintPK_AI_2(entity);
	}
	
	/**
	 * Checks specification constraint [PK_1]: At least one primary key field.
	 * 
	 * @param entity
	 * @throws GeneratorException
	 */
	private static void checkSpecConstraintPK_1(Entity entity) throws GeneratorException {
		if (entity.getPkAttributes().size() == 0) {
			throw new GeneratorException("Entity definition error: primary key definition missing. VOF-Class: " +
					entity.getVofSimpleClassName() + " Violation [ConstPK_1]");
		}
	}
	
	/**
	 * Checks specification constraint [PK_AI_1]: Maximum number of auto-incremnt fields is 1.
	 * 
	 * @param entity
	 * @throws GeneratorException
	 */
	private static void checkSpecConstraintPK_AI_1(Entity entity) throws GeneratorException {
		List<Attribute> attributeList = entity.getAttributes();
		
		int nrAutoIncAttrib = 0;
		for (Attribute attribute : attributeList) {
			if (attribute.isAutoIncrement()) {
				nrAutoIncAttrib++;				
			}
		}
		
		if (nrAutoIncAttrib > 1) {
			throw new GeneratorException("Entity definition error: multiple definitions of auto-increment fields. " +
					"VOF-Class: " + entity.getVofSimpleClassName() + " Violation [ConstPK_AI_1]");
		}
	}
	
	/**
	 * Checks specification constraint [PK_AI_2]: Auto-increment field must be of SQL-type INTEGER.
	 * 
	 * @param entity
	 * @throws GeneratorException
	 */
	private static void checkSpecConstraintPK_AI_2(Entity entity) throws GeneratorException {
		Attribute autoIncAttribute = entity.getAutoIncrementAttribute();
		if (autoIncAttribute != null) {
			String sqlDataType = autoIncAttribute.getSqlDataType().toUpperCase();
			if (!sqlDataType.equals("INTEGER")) {
				throw new GeneratorException("Entity definition error: Invalid type of auto-increment-field. " +
						"VOF-Class: " + entity.getVofSimpleClassName() + " Attribute name: " + autoIncAttribute.getFieldName() + " Violation [ConstPK_AI_2]");				
			}
		}
	}

	

}
