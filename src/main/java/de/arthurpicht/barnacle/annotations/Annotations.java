package de.arthurpicht.barnacle.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class Annotations {
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.FIELD})	
	public @interface Barnacle {}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface TableName {
		String value();
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)	
	public @interface VobFactory {}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface Cloneable{}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface ColumnName {
		String value();
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface PrimaryKey {
		boolean autoIncrement() default false;
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface NotNull {		
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface Default {
		String value();		
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface Type {
		String type();
		String para1() default "";
		String para2() default "";
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface Unique {
		String name() default "";
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface ForeignKey {
		String[] referenceTableName();
		String[] referenceColumnName();
		String[] foreignKeyName() default "";
		boolean[] onDeleteCascade() default false;
		boolean[] onUpdateCascade() default false;
		boolean[] getEntityMethod() default false;
		boolean[] setEntityMethod() default false;
		String[] entityMethodName() default "";
		boolean[] getReferenceEntityMethod() default false;
		boolean[] setReferenceEntityMethod() default false;
		String[] referenceEntityMethodName() default "";		
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface AssociationTable {
		String foreignKeyNameA();
		String foreignKeyNameB();
	}

}
