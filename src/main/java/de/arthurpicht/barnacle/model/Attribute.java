package de.arthurpicht.barnacle.model;

import de.arthurpicht.barnacle.annotations.Annotations;
import de.arthurpicht.barnacle.codeGenerator.sql.TypeMapper;
import de.arthurpicht.barnacle.exceptions.UnknownTypeException;

import java.lang.reflect.Field;

public class Attribute {

    private final Field field;
    private final String columnName;
    private final boolean isPrimaryKey;
    private final boolean isAutoIncrement;
    private final String defaultValue;
    private final boolean notNull;
    private final String sqlDataType;
    private final String sqlDataTypeLiteral;

    public Attribute(Field field, Entity entity, TypeMapper typeMapper) {

        this.field = field;

        // determine column name
        // set as field name if not given
        String columnName;
        if (field.isAnnotationPresent(Annotations.ColumnName.class)) {
            Annotations.ColumnName columnNameAnnotation = field.getAnnotation(Annotations.ColumnName.class);
            columnName = columnNameAnnotation.value();
        } else {
            columnName = this.getFieldName();
        }
        this.columnName = columnName;

        // determine PK
        boolean isPrimaryKey = false;
        boolean isAutoIncrement = false;
        if (field.isAnnotationPresent(Annotations.PrimaryKey.class)) {
            isPrimaryKey = true;
            Annotations.PrimaryKey primaryKey = field.getAnnotation(Annotations.PrimaryKey.class);
            isAutoIncrement = primaryKey.autoIncrement();
        }
        this.isPrimaryKey = isPrimaryKey;
        this.isAutoIncrement = isAutoIncrement;

        // determine 'not null' flag
        this.notNull = field.isAnnotationPresent(Annotations.NotNull.class);

        // determine default value
        String defaultValue;
        if (field.isAnnotationPresent(Annotations.Default.class)) {
            Annotations.Default defaultAnnotation = field.getAnnotation(Annotations.Default.class);
            defaultValue = defaultAnnotation.value();
            this.defaultValue = defaultValue;
        } else {
            this.defaultValue = null;
        }

        // determine custom type
        String type = null;
        Integer para1 = null;
        Integer para2 = null;
        if (field.isAnnotationPresent(Annotations.Type.class)) {
            Annotations.Type typeAnnotation = field.getAnnotation(Annotations.Type.class);
            type = typeAnnotation.type();
            String para1String = typeAnnotation.para1();
            if (!para1String.equals("")) {
                para1 = Integer.valueOf(para1String);

                String para2String = typeAnnotation.para2();
                if (!para2String.equals("")) {
                    para2 = Integer.valueOf(para2String);
                }
            }
        }

        // unique
        // TODO move this to class Entity
        if (field.isAnnotationPresent(Annotations.Unique.class)) {
            Annotations.Unique uniqueAnnotation = field.getAnnotation(Annotations.Unique.class);
            String indexName = uniqueAnnotation.name();
            if (indexName.equals("")) {
                entity.addUniqueField("uk_" + this.columnName, this);
            } else {
                entity.addUniqueField(indexName, this);
            }
        }

        try {
            if (type == null) {
                this.sqlDataType = typeMapper.getSqlTypeByAutoMapping(field.getType());
                this.sqlDataTypeLiteral = typeMapper.getSqlTypeLiteralByAutoMapping(field.getType());
            } else {
                this.sqlDataType = typeMapper.getSqlTypeByCustomType(type, para1, para2);
                this.sqlDataTypeLiteral = typeMapper.getSqlTypeLiteralByCustomType(type);
            }
        } catch (UnknownTypeException e) {
            throw new ERMBuilderException(e);
        }
    }

    public Class<?> getType() {
        return this.field.getType();
    }

    public String getTypeLiteral() {
        if (isJavaTypeSimple()) throw new IllegalStateException("No type literal available for simple type.");
        return getJavaTypeSimpleName() + ".class";
    }

    public String getJavaTypeSimpleName() {
        return this.field.getType().getSimpleName();
    }

    public String getJavaTypeCanonicalName() {
        return this.field.getType().getCanonicalName();
    }

    public boolean isJavaTypeSimple() {
        return !getJavaTypeCanonicalName().startsWith("java.lang.");
    }

    public String getFieldName() {
        return this.field.getName();
    }

    public String getColumnName() {
        return columnName;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public boolean isAutoIncrement() {
        return this.isAutoIncrement;
    }

    /**
     * Returns the default value for the intended column.
     * Null means no default, empty string means explicit
     * empty string as default.
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    public String generateGetterMethodName() {
        String methodName = "get";
        methodName += this.getFieldNameWithFirstLetterUpperCase();
        return methodName;
    }

    public String generateSetterMethodName() {
        String methodName = "set";
        methodName += this.getFieldNameWithFirstLetterUpperCase();
        return methodName;
    }

    private String getFieldNameWithFirstLetterUpperCase() {
        String methodName = this.getFieldName().substring(0, 1).toUpperCase();
        methodName += this.getFieldName().substring(1);
        return methodName;
    }

    /**
     * Gets SQL-Datatype.
     */
    public String getSqlDataType() {
        return this.sqlDataType;
    }

    public String getSqlDataTypeLiteral() {
        return this.sqlDataTypeLiteral;
    }

    /**
     * Determines whether this attribute represents a java
     * primitive type field.
     */
    public boolean isPrimitiveType() {
        return this.field.getType().isPrimitive();
    }

    /**
     * Checks for equality. Two attributes are defined as equal, if they share
     * the same field name.
     */
    public boolean equals(Attribute attribute) {
        return this.getFieldName().equals(attribute.getFieldName());
    }

    public String toString() {
        String string = "Column fieldTypeSimpleName=" + this.getJavaTypeSimpleName() + " fieldName=" + this.getFieldName() + " columnName=" + this.columnName + " isPK=" + this.isPrimaryKey + " isAutoIncrement=" + isAutoIncrement;
        if (this.notNull) {
            string += " not null";
        }
        return string;
    }

}
