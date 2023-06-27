package de.arthurpicht.barnacle.codeGenerator.java.vo;

import de.arthurpicht.barnacle.codeGenerator.CodeGeneratorException;
import de.arthurpicht.barnacle.codeGenerator.java.JavaGeneratorHelper;
import de.arthurpicht.barnacle.codeGenerator.java.MethodGenerator;
import de.arthurpicht.barnacle.configuration.generator.GeneratorConfiguration;
import de.arthurpicht.barnacle.helper.StringHelper;
import de.arthurpicht.barnacle.model.*;
import de.arthurpicht.console.Console;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class VoGenerator extends VoBaseGenerator {

    private final String connectionExceptionCanonicalClassName;
    private final String entityNotFoundExceptionCanonicalClassName;

    public VoGenerator(
            Entity entity,
            EntityRelationshipModel entityRelationshipModel,
            GeneratorConfiguration generatorConfiguration) throws CodeGeneratorException {

        super(getVOCanonicalClassNameFromEntity(entity, generatorConfiguration), entity, generatorConfiguration);

        Console.verbose("Generating VO class [" + entity.getVoCanonicalClassName() + "].");

        this.connectionExceptionCanonicalClassName
                = generatorConfiguration.getConnectionExceptionCanonicalClassName();
        this.entityNotFoundExceptionCanonicalClassName
                = generatorConfiguration.getEntityNotFoundExceptionCanonicalClassName();

        // imports for non-primitive Attributes
        List<Attribute> attributeList = entity.getAttributes();
        for (Attribute attribute : attributeList) {
            if (!attribute.isPrimitiveType()) {
                this.getImportGenerator().addImport(attribute.getJavaTypeCanonicalName());
            }
        }

        // constants
        if (entity.isSerializable()) {
            this.addImplementedInterface(Serializable.class);
            this.constantGenerator.addPrivateLongConstant("serialVersionUID", entity.getSerialVersionUID());
        }

        this.constantGenerator.addStringConstant("TABLENAME", entity.getTableName());

        List<Attribute> constantAttributes = entity.getAttributes();
        this.addConstants(constantAttributes);

        // constructor
        this.addConstructor();

        // pk-getter
        if (entity.isComposedPk()) {
            this.addPkGetter();
        }

        // getter & setter methods
        List<Attribute> allAttributes = entity.getAttributes();
        this.addGetterSetter(allAttributes);

        // toString method
        this.addToStringMethod();

        // clone method
        if (entity.isCloneable()) this.addCloneMethod();

        // foreign key getter methods
        Set<ForeignKeyWrapper> foreignKeyWrapperSet = entity.getAllForeignKeys();
        for (ForeignKeyWrapper foreignKeyWrapper : foreignKeyWrapperSet) {
            if (foreignKeyWrapper.isGetEntityMethod()) {
                this.addFkGetter(foreignKeyWrapper);
            }
        }

        // getter method for referencing foreign keys
        Set<ForeignKeyWrapper> referencingForeignKeyWrapperSet
                = Entities.getAllReferencingForeignKeys(entity, entityRelationshipModel);
        for (ForeignKeyWrapper referencingForeignKeyWrapper : referencingForeignKeyWrapperSet) {
            this.addReferencingFkGetter(referencingForeignKeyWrapper);
        }

        // VOB-factory
        if (this.entity.isVobFactoryMethod()) {
            this.addVobFactory();
        }

        // equals method
        this.addEqualsMethod();
    }

    private void addConstructor() throws CodeGeneratorException {

        List<Attribute> attributes = this.entity.getPkAttributes();
        VoConstructorGenerator constructorGenerator = this.getVOConstructorGenerator();
        if (this.entity.getNrPkAttributes() > 1) {
            // constructor with PK-object as parameter.
            String pkSimpleClassName = this.entity.getPkSimpleClassName();
            String pkVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(pkSimpleClassName);
            constructorGenerator.defineParameter(pkSimpleClassName, pkVarName);
            for (Attribute attribute : attributes) {
                String member = "this." + attribute.getFieldName();
                String assignment = pkVarName + "." + attribute.generateGetterMethodName() + "()";
                constructorGenerator.defineAssignment(member, assignment);
            }

        } else {
            // simple constructor with one attribute as parameter.
            constructorGenerator.defineParametersAndAssignmentsByAttributes(attributes);

        }
    }

    private void addFkGetter(ForeignKeyWrapper foreignKeyWrapper) {

        Entity referenceEntity = foreignKeyWrapper.getTargetEntity();
        String referenceVoSimpleClassName = referenceEntity.getVoSimpleClassName();
        String referenceVoCanonicalClassName = referenceEntity.getVoCanonicalClassName();
        boolean hasReferenceComposedPk = foreignKeyWrapper.getTargetEntity().isComposedPk();

        this.getImportGenerator().addImport(referenceVoCanonicalClassName);

        MethodGenerator fkGetterGenerator = this.getNewMethodGenerator();
        fkGetterGenerator.setReturnTypeBySimpleClassName(referenceVoSimpleClassName);
        fkGetterGenerator.setMethodName(foreignKeyWrapper.getEntityMethodName());
        fkGetterGenerator.addThrowsException(this.connectionExceptionCanonicalClassName);
        fkGetterGenerator.addThrowsException(this.entityNotFoundExceptionCanonicalClassName);

        if (hasReferenceComposedPk) {

            String pkSimpleClassName = referenceEntity.getPkSimpleClassName();
            String pkVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(pkSimpleClassName);
            List<Attribute> referencePkAttributes = referenceEntity.getPkAttributes();

            StringBuilder lineOfCode = new StringBuilder(pkSimpleClassName + " "
                    + pkVarName
                    + " = new " + pkSimpleClassName + "(");

            boolean sequence = false;
            for (Attribute referencePkAttribute : referencePkAttributes) {
                if (sequence) {
                    lineOfCode.append(", ");
                }
                Attribute localAttribute
                        = foreignKeyWrapper.getKeyFieldAttributeByReferencedFieldAttribute(referencePkAttribute);
                lineOfCode.append("this.").append(localAttribute.getFieldName());
                sequence = true;
            }

            lineOfCode.append(");");
            fkGetterGenerator.addCodeLn(lineOfCode.toString());

            fkGetterGenerator.addCodeLn("return " + referenceEntity.getDaoSimpleClassName()
                    + ".load(" + pkVarName + ");");

        } else {
            Attribute referencePkAttribute = referenceEntity.getSinglePkAttribute();
            Attribute localAttribute
                    = foreignKeyWrapper.getKeyFieldAttributeByReferencedFieldAttribute(referencePkAttribute);
            fkGetterGenerator.addCodeLn("return " + referenceEntity.getDaoSimpleClassName() + ".load(this."
                    + localAttribute.getFieldName() + ");");
        }

        this.importGenerator.addImport(referenceEntity.getDaoCanonicalClassName());
    }

    public void addReferencingFkGetter(ForeignKeyWrapper referencingForeignKeyWrapper) {
        String voCanonicalClassName = referencingForeignKeyWrapper.getParentEntity().getVoCanonicalClassName();
        String daoSimpleClassName = referencingForeignKeyWrapper.getParentEntity().getDaoSimpleClassName();
        String daoCanonicalClassName = referencingForeignKeyWrapper.getParentEntity().getDaoCanonicalClassName();
        String fkNameShiftedCase
                = StringHelper.shiftFirstLetterToUpperCase(referencingForeignKeyWrapper.getForeignKeyName());

        this.getImportGenerator().addImport(daoCanonicalClassName);

        String methodName = referencingForeignKeyWrapper.getReferencedEntityMethodName();
        MethodGenerator methodGenerator = this.getNewMethodGenerator();
        methodGenerator.setReturnType(List.class);
        methodGenerator.setReturnTypeParameter(voCanonicalClassName);
        methodGenerator.setMethodName(methodName);
        methodGenerator.addThrowsException(this.connectionExceptionCanonicalClassName);

        methodGenerator.addCode("return " + daoSimpleClassName + ".findBy" + fkNameShiftedCase + "(");

        boolean sequence = false;
        for (Attribute attribute : referencingForeignKeyWrapper.getTargetFieldAttributes()) {
            if (sequence) {
                methodGenerator.addCode(", ");
            }
            methodGenerator.addCode("this." + attribute.getFieldName());
            sequence = true;
        }

        methodGenerator.addCodeLn(");");
    }

    private void addCloneMethod() {
        this.addImplementedInterface(Cloneable.class);
        this.importGenerator.addImport(InternalError.class);
        this.importGenerator.addImport(CloneNotSupportedException.class);

        MethodGenerator cloneMethodGenerator = this.getNewMethodGenerator();
        cloneMethodGenerator.setMethodName("clone");
        cloneMethodGenerator.setReturnType(Object.class);

        cloneMethodGenerator.addCodeLn("try {");
        cloneMethodGenerator.addCodeLn("return super.clone();");
        cloneMethodGenerator.addCodeLn("} catch (CloneNotSupportedException e) {");
        cloneMethodGenerator.addCodeLn("throw new InternalError();");
        cloneMethodGenerator.addCodeLn("}");
    }

    private void addToStringMethod() {
        MethodGenerator methodGenerator = this.getNewMethodGenerator();
        methodGenerator.setMethodName("toString");
        methodGenerator.setReturnType(String.class);

        methodGenerator.addCodeLn("return \"" + this.entity.getVoCanonicalClassName() + "[\"");

        List<Attribute> attributeList = this.entity.getAttributes();
        boolean sequence = false;
        for (Attribute attribute : attributeList) {
            if (sequence) {
                methodGenerator.addCodeLn(" + \"; \"");
            }
            methodGenerator.addCode("+ \"" + attribute.getFieldName() + "=\" + this." + attribute.getFieldName());
            sequence = true;
        }
        methodGenerator.addCodeLn("");
        methodGenerator.addCodeLn("+ \"]\";");
    }

    private void addVobFactory() {
        String vobCanonicalClassName = this.entity.getVobCanonicalClassName();
        String vobSimpleClassName = this.entity.getVobSimpleClassName();
        String vobVarName = JavaGeneratorHelper.getVarNameFromSimpleClassName(vobSimpleClassName);

        MethodGenerator methodGenerator = this.getNewMethodGenerator();
        methodGenerator.setMethodName("get" + vobSimpleClassName);
        methodGenerator.setReturnTypeByCanonicalClassName(vobCanonicalClassName);

        methodGenerator.addCode(vobSimpleClassName + " " + vobVarName + " = new " + vobSimpleClassName);
        if (this.entity.isComposedPk()) {
            methodGenerator.addCodeLn("(this.getPK());");
        } else {
            Attribute pkAttribute = this.entity.getSinglePkAttribute();
            methodGenerator.addCodeLn("(this." + pkAttribute.generateGetterMethodName() + "());");
        }

        List<Attribute> nonPkAttributeList = this.entity.getNonPkAttributes();
        for (Attribute attribute : nonPkAttributeList) {
            methodGenerator.addCodeLn(vobVarName + "." + attribute.generateSetterMethodName()
                    + "(this." + attribute.getFieldName() + ");");
        }
        methodGenerator.addCodeLn("return " + vobVarName + ";");
    }

    private void addEqualsMethod() {
        MethodGenerator methodGenerator = this.getNewMethodGenerator();
        methodGenerator.setMethodName("equals");
        methodGenerator.setReturnTypeBySimpleClassName("boolean");
        methodGenerator.addParameter(Object.class, "o");

        methodGenerator.addCodeLn("if (o == null) return false;");
        methodGenerator.addCodeLn("if (o == this) return true;");
        methodGenerator.addCodeLn("if (o.getClass() != this.getClass()) return false;");

        String voSimpleClassName = this.entity.getVoSimpleClassName();
        methodGenerator.addCodeLn(voSimpleClassName + " other = (" + voSimpleClassName + ") o;");

        List<Attribute> attributeList = this.entity.getAttributes();
        for (Attribute attribute : attributeList) {
            if (attribute.isPrimitiveType()) {
                methodGenerator.addCodeLn("if (this." + attribute.getFieldName() + " != other."
                        + attribute.generateGetterMethodName() + "()) return false;");
            } else {
                methodGenerator.addCodeLn("if (this." + attribute.getFieldName() + " == null) {");
                methodGenerator.addCodeLn("if (other." + attribute.generateGetterMethodName()
                        + "() != null) return false;");
                methodGenerator.addCodeLn("} else {");
                methodGenerator.addCodeLn("if (!this." + attribute.getFieldName()
                        + ".equals(other." + attribute.generateGetterMethodName() + "())) return false;");
                methodGenerator.addCodeLn("}");
            }
        }
        methodGenerator.addCodeLn("return true;");
    }

    private static String getVOCanonicalClassNameFromEntity(Entity entity, GeneratorConfiguration generatorConfiguration) {
        String vofClassName = entity.getVofClass().getSimpleName();
        String voSimpleClassName = vofClassName.substring(0, vofClassName.length()-3) + "VO";
        return generatorConfiguration.getVoPackageName() + "." + voSimpleClassName;
    }

}
