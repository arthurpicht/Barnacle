package de.arthurpicht.barnacle.processor;

import de.arthurpicht.barnacle.annotations.Annotations.AssociationTable;
import de.arthurpicht.barnacle.model.ERMBuilderException;
import de.arthurpicht.barnacle.model.Entity;
import de.arthurpicht.barnacle.model.ForeignKeyWrapper;

public class VOFProcessorStage3 {
	
	public static void process(Entity entity) {
		
		Class<?> vofClass = entity.getVofClass();
		
		if (vofClass.isAnnotationPresent(AssociationTable.class)) {
			
			AssociationTable associationTable = vofClass.getAnnotation(AssociationTable.class);
			String foreignKeyA = associationTable.foreignKeyNameA();
			String foreignKeyB = associationTable.foreignKeyNameB();
			
			ForeignKeyWrapper foreignKeyWrapperA = entity.getForeignKeyByName(foreignKeyA);
			if (foreignKeyWrapperA == null) {
				throw new ERMBuilderException("Foreign key referenced as part A of association does not exist. Entity= "
						+ entity.getTableName() + "; ForeignKey= " + foreignKeyA
						+ " Violation [ConstAT_1]");
			}
			
			ForeignKeyWrapper foreignKeyWrapperB = entity.getForeignKeyByName(foreignKeyB);
			if (foreignKeyWrapperB == null) {
				throw new ERMBuilderException("Foreign key referenced as part A of association does not exist. Entity= "
						+ entity.getTableName() + "; ForeignKey= " + foreignKeyA
						+ " Violation [ConstAT_1]");
			}
			
			entity.setAssociationTable(true);
			entity.setAssociationForeignKeyA(foreignKeyWrapperA);
			entity.setAssociationForeignKeyB(foreignKeyWrapperB);
		}

	}
}
