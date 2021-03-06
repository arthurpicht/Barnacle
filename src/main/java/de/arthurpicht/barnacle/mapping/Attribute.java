package de.arthurpicht.barnacle.mapping;

import de.arthurpicht.barnacle.BarnacleInitializer;
import de.arthurpicht.barnacle.annotations.Annotations;
import de.arthurpicht.barnacle.exceptions.GeneratorException;
import de.arthurpicht.barnacle.exceptions.UnknownTypeException;
import de.arthurpicht.barnacle.generator.sql.TypeMapper;

import java.lang.reflect.Field;

/**
 * Representing mapped attribute: object field and DB column.
 * <p>
 * <p>
 * This code is part of Barnacle: https://github.com/arthurpicht/Barnacle *
 */
public class Attribute {

    private Field field;

    private String columnName;

    private boolean isPrimaryKey;
    private boolean isAutoIncrement;

    private String defaultValue;

    private String type;
    private Integer para1;
    private Integer para2;

    private boolean notNull;

    private String sqlDataType;

    public Attribute(Field field, Entity entity) throws GeneratorException {

        this.field = field;

        // determine columnname
        // set as fieldname if not given
        String columnName = new String();
        if (field.isAnnotationPresent(Annotations.ColumnName.class)) {
            Annotations.ColumnName columnNameAnnotation = (Annotations.ColumnName) field.getAnnotation(Annotations.ColumnName.class);
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
            Annotations.PrimaryKey primaryKey = (Annotations.PrimaryKey) field.getAnnotation(Annotations.PrimaryKey.class);
            isAutoIncrement = primaryKey.autoIncrement();
        }
        this.isPrimaryKey = isPrimaryKey;
        this.isAutoIncrement = isAutoIncrement;

        // determine 'not null' flag
        boolean notNull = false;
        if (field.isAnnotationPresent(Annotations.NotNull.class)) {
            notNull = true;
        }
        this.notNull = notNull;

        // determine default value
        String defaultValue = new String();
        if (field.isAnnotationPresent(Annotations.Default.class)) {
            Annotations.Default defaultAnnotation = (Annotations.Default) field.getAnnotation(Annotations.Default.class);
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
            Annotations.Type typeAnnotation = (Annotations.Type) field.getAnnotation(Annotations.Type.class);
            type = typeAnnotation.type();
            String para1String = typeAnnotation.para1();
            if (!para1String.equals("")) {
                para1 = new Integer(para1String);

                String para2String = typeAnnotation.para2();
                if (!para2String.equals("")) {
                    para2 = new Integer(para2String);
                }
            }
        }
        this.type = type;
        this.para1 = para1;
        this.para2 = para2;

        // unique
        // TODO move this to class Entity
        if (field.isAnnotationPresent(Annotations.Unique.class)) {
            Annotations.Unique uniqueAnnotation = (Annotations.Unique) field.getAnnotation(Annotations.Unique.class);
            String indexName = uniqueAnnotation.name();
            if (indexName.equals("")) {
                entity.addUniqueField("uk_" + this.columnName, this);
            } else {
                entity.addUniqueField(indexName, this);
            }
        }

        // Determine SQL datatype by requesting database specific TypeMapper
        String sqlDataType;
        TypeMapper typeMapper = TypeMapper.getInstance(BarnacleInitializer.getDatabase());
        try {
            sqlDataType = typeMapper.getSQLType(this);
        } catch (UnknownTypeException e) {
            throw new GeneratorException(e);
        }
        this.sqlDataType = sqlDataType;
    }

    /**
     * Returns java type for represented field.
     *
     * @return
     */
    public String getFieldTypeSimpleName() {
        return this.field.getType().getSimpleName();
    }

    public String getFieldTypeCanonicalName() {
        return this.field.getType().getCanonicalName();
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
     *
     * @return
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Determines if a custom type is defined for this
     * attribute.
     *
     * @return
     */
    public boolean hasCustomType() {
        if (this.type != null) {
            return true;
        }
        return false;
    }

    /**
     * Returns the cusom type string, defined using the TYPE-
     * annotation.
     *
     * @return
     */
    public String getCustomType() {
        String typeString = this.type;
        if (this.para1 != null) {
            typeString += "(" + this.para1;
            if (this.para2 != null) {
                typeString += ", " + this.para2;
            }
            typeString += ")";
        }
        return typeString;
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
        methodName += this.getFieldName().substring(1, this.getFieldName().length());
        return methodName;
    }

    /**
     * Returns the name of the public constant as part of the value objects.
     * Constant name is defined as attribute`s field name in uppercase letters.
     * The values given in the VOs represent the corresponding column names.
     *
     * @return
     */
    public String getConstName() {
        return this.getFieldName().toUpperCase();
    }

    /**
     * Gets SQL-Datatype.
     *
     * @return
     */
    public String getSqlDataType() {
        return this.sqlDataType;
    }

    /**
     * Determines whether this attribute represents a java
     * primitive type field.
     *
     * @return
     */
    public boolean isPrimitiveType() {
        return this.field.getType().isPrimitive();
    }

    /**
     * Checks for equality. Two attributes are defined as equal, if they share
     * the same field name.
     *
     * @param attribute
     * @return
     */
    public boolean equals(Attribute attribute) {
        if (this.getFieldName().equals(attribute.getFieldName())) {
            return true;
        }
        return false;
    }

    public String toString() {
        String string = "Column fieldTypeSimpleName=" + this.getFieldTypeSimpleName() + " fieldName=" + this.getFieldName() + " columnName=" + this.columnName + " isPK=" + this.isPrimaryKey + " isAutoIncrement=" + isAutoIncrement;
        if (this.notNull) {
            string += " not null";
        }
        return string;
    }

}
