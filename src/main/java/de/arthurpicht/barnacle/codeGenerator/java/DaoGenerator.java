package de.arthurpicht.barnacle.codeGenerator.java;

import de.arthurpicht.barnacle.configuration.generator.GeneratorConfiguration;
import de.arthurpicht.barnacle.context.GeneratorContext;
import de.arthurpicht.barnacle.codeGenerator.sql.TypeMapper;
import de.arthurpicht.barnacle.helper.Helper;
import de.arthurpicht.barnacle.model.Attribute;
import de.arthurpicht.barnacle.model.Entity;
import de.arthurpicht.barnacle.model.ForeignKeyWrapper;
import de.arthurpicht.utils.core.strings.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DaoGenerator extends ClassGenerator {

    private static final Logger logger = LoggerFactory.getLogger("BARNACLE");

    private final Entity entity;
    private final String connectionManagerSimpleClassName;
    private final String connectionExceptionCanonicalClassName;
    private final String connectionExceptionSimpleClassName;
    private final String entityNotFoundExceptionCanonicalClassName;
    private final String entityNotFoundExceptionSimpleClassName;

    public DaoGenerator(Entity entity) {
        super(entity.getDaoCanonicalClassName());

        logger.debug("Assembling class " + entity.getDaoSimpleClassName());

        this.entity = entity;

        String connectionManagerCanonicalClassName = GeneratorContext.getInstance().getGeneratorConfiguration().getConnectionManagerCanonicalClassName();
        this.connectionManagerSimpleClassName = Helper.getSimpleClassNameFromCanonicalClassName(connectionManagerCanonicalClassName);
        this.connectionExceptionCanonicalClassName = getGeneratorConfiguration().getConnectionExceptionCanonicalClassName();
        this.connectionExceptionSimpleClassName = Helper.getSimpleClassNameFromCanonicalClassName(this.connectionExceptionCanonicalClassName);
        this.entityNotFoundExceptionCanonicalClassName = getGeneratorConfiguration().getEntityNotFoundExceptionCanonicalClassName();
        this.entityNotFoundExceptionSimpleClassName = Helper.getSimpleClassNameFromCanonicalClassName(this.entityNotFoundExceptionCanonicalClassName);

        // standard imports
        this.addStandardImports();
        this.addImportsForNonPrimitiveAttributes();

        // standard methods
        this.addCreateMethod();
        this.addCreateMethodByConnectionPrep();
        this.addCreateMethodByConnection();
        this.addCreateMethodBatch();

        this.addLoadMethod();
        this.addLoadMethodByConnection();

        this.addDeleteMethod();
        this.addDeleteMethodByConnection();

        this.addUpdateMethod();
        this.addUpdateMethodByConnection();

        this.addFindAllMethod();

        // one finder method for each foreign key
        for (ForeignKeyWrapper foreignKeyWrapper : this.entity.getAllForeignKeys()) {
            this.addFindByForeignKeyMethod(foreignKeyWrapper);
        }

        // one finder methode for each unique key
        Set<String> uniqueKeyNameList = this.entity.getAllUniqueIndicesNames();
        for (String uniqueKeyName : uniqueKeyNameList) {
            List<Attribute> uniqueKeyAttributes = this.entity.getAttributesByUniqueIndexName(uniqueKeyName);
            this.addFindByUniqueKeyMethod(uniqueKeyName, uniqueKeyAttributes);
        }

        // two finder methods for association
        // addAssociation method for association
        if (this.entity.isAssociationTable()) {
            this.addGetAssociatedEntites(this.entity.getAssociationForeignKeyA(), this.entity.getAssociationForeignKeyB());
            this.addGetAssociatedEntites(this.entity.getAssociationForeignKeyB(), this.entity.getAssociationForeignKeyA());

            this.addAssociation(this.entity.getAssociationForeignKeyA(), this.entity.getAssociationForeignKeyB());
        }

        this.addGetValueExpressionMethod();
    }

    private void addStandardImports() {
        this.importGenerator.addImport(
                GeneratorContext.getInstance().getGeneratorConfiguration().getConnectionManagerCanonicalClassName());
        this.importGenerator.addImport(List.class);
        this.importGenerator.addImport(ArrayList.class);
        this.importGenerator.addImport(Connection.class);
        this.importGenerator.addImport(Statement.class);
        this.importGenerator.addImport(ResultSet.class);
        this.importGenerator.addImport(SQLException.class);
        this.importGenerator.addImport(PreparedStatement.class);
    }

    private void addImportsForNonPrimitiveAttributes() {
        List<Attribute> attributeList = entity.getAttributes();
        for (Attribute attribute : attributeList) {
            if (!attribute.isPrimitiveType()) {
                this.getImportGenerator().addImport(attribute.getJavaTypeCanonicalName());
            }
        }
    }

    private void addCreateMethod() {
        MethodGenerator methodGenerator = this.getNewMethodGenerator();
        methodGenerator.setIsStatic(true);
        methodGenerator.setMethodName("create");

        String voSimpleClassName = this.entity.getVoSimpleClassName();
        String voCanonicalClassName = this.entity.getVoCanonicalClassName();
        String voVarName = generateVarNameFromSimpleClassName(voSimpleClassName);

        methodGenerator.addAndImportParameter(voCanonicalClassName, voVarName);

        methodGenerator.addThrowsException(this.connectionExceptionCanonicalClassName);

        methodGenerator.addCodeLn(createGetConnectionStatement());
        methodGenerator.addCodeLn("try {");
        methodGenerator.addCodeLn("create(" + voVarName + ", connection);");
        methodGenerator.addCodeLn("} catch (" + SQLException.class.getSimpleName() + " sqlEx) {");
        methodGenerator.addCodeLn("throw new " + Helper.getSimpleClassNameFromCanonicalClassName(this.connectionExceptionCanonicalClassName) + "(sqlEx);");
        methodGenerator.addCodeLn("} finally {");
        methodGenerator.addCodeLn(generateReleaseConnectionStatement());
        methodGenerator.addCodeLn("}");
    }

    private void addCreateMethodByConnectionPrep() {

        addPreparedStatementCreateAsLocalConst();

        MethodGenerator methodGenerator = this.getNewMethodGenerator();
        methodGenerator.setIsStatic(true);
        methodGenerator.setMethodName("create");

        String voSimpleClassName = this.entity.getVoSimpleClassName();
        String voCanonicalClassName = this.entity.getVoCanonicalClassName();
        String voVarName = generateVarNameFromSimpleClassName(voSimpleClassName);
        methodGenerator.addAndImportParameter(voCanonicalClassName, voVarName);
        methodGenerator.addAndImportParameter(Connection.class, "connection");

        methodGenerator.addThrowsException(SQLException.class);

        methodGenerator.addCodeLn(
                "PreparedStatement preparedStatement = connection.prepareStatement(CREATE_STATEMENT);");

        int index = 1;
        List<String> getterList = new ArrayList<>();
        for (Attribute attribute : entity.getNonAutoIncrementAttributes()) {
            String setMethod = TypeMapper.getPreparedStatementSetMethod(attribute.getJavaTypeSimpleName());
            String getter = voVarName + "." + attribute.generateGetterMethodName() + "()";
            getterList.add(getter);
            String line = "preparedStatement." + setMethod
                    + "(" + index + ", "
                    + getter + ");";
            methodGenerator.addCodeLn(line);
            index++;
        }

        String logStatement = Strings.listing(getterList, " + \"][\" + ", "CREATE_STATEMENT + \" [\" + ", " + \"]\"");
        LoggerGenerator loggerGenerator = this.getLoggerGenerator();
        methodGenerator.addCodeLn(loggerGenerator.generateDebugLogStatementByExpression(logStatement));

        methodGenerator.addCodeLn("preparedStatement.executeUpdate();");

        if (entity.hasAutoIncrementAttribute()) {
            Attribute autoIncrementAttribute = this.entity.getAutoIncrementAttribute();
            methodGenerator.addCodeLn("ResultSet resultSet = preparedStatement.getGeneratedKeys();");
            methodGenerator.addCodeLn("if (resultSet.next()) {");
            methodGenerator.addCodeLn("int generatedKey = resultSet.getInt(1);");
            methodGenerator.addCodeLn(voVarName + "." + autoIncrementAttribute.generateSetterMethodName()
                    + "(generatedKey);");
            methodGenerator.addCodeLn("} else {");
            methodGenerator.addCodeLn("throw new SQLException(\"Could not obtain generated key for " +
                    "auto increment field.\");");
            methodGenerator.addCodeLn("}");
            methodGenerator.addCodeLn("try { resultSet.close(); } catch (SQLException ignored) {}");
        }

        methodGenerator.addCodeLn("try { preparedStatement.close(); } catch (SQLException ignored) {}");
    }

    private void addPreparedStatementCreateAsLocalConst() {
        String statement = "INSERT INTO " + this.entity.getTableName();

        List<Attribute> attributes = this.entity.getNonAutoIncrementAttributes();
        List<String> columnNames = attributes.stream()
                .map(Attribute::getColumnName)
                .collect(Collectors.toList());
        String columnNameListing = Strings.listing(columnNames, ", ", " (", ") ");
        statement += columnNameListing;

        statement += "VALUES";

        List<String> questionMarks = attributes.stream()
                .map(a -> "?")
                .collect(Collectors.toList());
        String questionMarkListing = Strings.listing(questionMarks, ", ", " (", ")");
        statement += questionMarkListing;

        this.localStringConstGenerator.add("CREATE_STATEMENT", statement);
    }

    private void addCreateMethodByConnection() {

        MethodGenerator methodGenerator = this.getNewMethodGenerator();
        methodGenerator.setIsStatic(true);
        methodGenerator.setMethodName("createOLD");

        String voSimpleClassName = this.entity.getVoSimpleClassName();
        String voCanonicalClassName = this.entity.getVoCanonicalClassName();
        String voVarName = generateVarNameFromSimpleClassName(voSimpleClassName);
        methodGenerator.addAndImportParameter(voCanonicalClassName, voVarName);
        methodGenerator.addAndImportParameter(Connection.class, "connection");

        methodGenerator.addThrowsException(SQLException.class);

        methodGenerator.addCodeLn("String sql=\"INSERT INTO \" + " + voSimpleClassName + ".TABLENAME + \" (\"");

//		List<Attribute> attributes = this.entity.getAttributes();
        List<Attribute> attributes = this.entity.getNonAutoIncrementAttributes();
        boolean sequence = false;
        for (Attribute attribute : attributes) {
            if (sequence) {
                methodGenerator.addCodeLn("\", \" ");
            }
//			methodGenerator.addCode("+ " + voSimpleClassName + "." + attribute.getFieldName().toUpperCase() + " + ");
            methodGenerator.addCode("+ " + voSimpleClassName + "." + attribute.getConstName() + " + ");
            sequence = true;
        }
        methodGenerator.addCodeLn("\") \"");

        methodGenerator.addCodeLn("+ \"VALUES (\"");
        sequence = false;
        for (Attribute attribute : attributes) {
            if (sequence) {
                methodGenerator.addCodeLn(" + \", \"");
            }
            methodGenerator.addCode("+ getValueExpression(" + voVarName + "." + attribute.generateGetterMethodName() + "(), \"" + attribute.getSqlDataType() + "\")");
            sequence = true;
        }
        methodGenerator.addCodeLn(" + \")\";");

        LoggerGenerator loggerGenerator = this.getLoggerGenerator();
        methodGenerator.addCodeLn(loggerGenerator.generateDebugLogStatementByVarName("sql"));

        methodGenerator.addCodeLn("Statement statement = connection.createStatement();");
        methodGenerator.addCodeLn("statement.execute(sql);");

        Attribute autoIncrementAttribute = this.entity.getAutoIncrementAttribute();
        // FIXME s. Bug 004
        // Was passiert wenn mehr als ein Attribut mit autoIncrement ausgezeichnet ist?
        if (autoIncrementAttribute != null) {
            methodGenerator.addCodeLn("");
            methodGenerator.addCodeLn("sql = \"SELECT last_insert_id() FROM \" + " + voSimpleClassName + ".TABLENAME;");
            methodGenerator.addCodeLn("logger.debug(sql);");
            methodGenerator.addCodeLn("ResultSet resultSet = statement.executeQuery(sql);");
            methodGenerator.addCodeLn("if (resultSet.next()) {");
            methodGenerator.addCodeLn("int lastInsertId = (Integer) resultSet.getInt(1);");
            methodGenerator.addCodeLn(voVarName + "." + autoIncrementAttribute.generateSetterMethodName() + "(lastInsertId);");
            methodGenerator.addCodeLn("} else {");
            methodGenerator.addCodeLn("throw new SQLException(\"Could not determine last_insert_id for auto increment field.\");");
            methodGenerator.addCodeLn("}");
            methodGenerator.addCodeLn("if (resultSet != null) { try { resultSet.close(); } catch (SQLException e) {}}");
        }

        methodGenerator.addCodeLn("if (statement != null) { try { statement.close(); } catch (SQLException e) {}}");

    }

    private void addCreateMethodBatch() {

        // batch create for non-autoincrement entities only
        if (this.entity.getAutoIncrementAttribute() != null) return;

        MethodGenerator methodGenerator = this.getNewMethodGenerator();
        methodGenerator.setIsStatic(true);
        methodGenerator.setMethodName("create");

        String voSimpleClassName = this.entity.getVoSimpleClassName();
        String voCanonicalClassName = this.entity.getVoCanonicalClassName();
        String voVarName = generateVarNameFromSimpleClassName(voSimpleClassName);
        String voListVarName = voVarName + "s";

//		methodGenerator.addAndImportParameter(voCanonicalClassName, voVarName);
        methodGenerator.addAndImportParameter(List.class, voCanonicalClassName, voListVarName);
//		methodGenerator.addAndImportParameter(Connection.class, "connection");

        methodGenerator.addThrowsException(this.connectionExceptionCanonicalClassName);

//        methodGenerator.addCodeLn("Connection connection = null;");
//        methodGenerator.addCodeLn("Connection connection = " + this.connectionManagerSimpleClassName + ".getInstance().openConnection(" + this.entity.getDaoSimpleClassName() + ".class);");
        methodGenerator.addCodeLn(createGetConnectionStatement());
        methodGenerator.addCodeLn("Statement statement = null;");
        methodGenerator.addCodeLn("try {");
        methodGenerator.addCodeLn("statement = connection.createStatement();");
        methodGenerator.addCodeLn("for (" + voSimpleClassName + " " + voVarName + " : " + voListVarName + ") {");

        methodGenerator.addCodeLn("String sql=\"INSERT INTO \" + " + voSimpleClassName + ".TABLENAME + \" (\"");

//		List<Attribute> attributes = this.entity.getAttributes();
        List<Attribute> attributes = this.entity.getNonAutoIncrementAttributes();
        boolean sequence = false;
        for (Attribute attribute : attributes) {
            if (sequence) {
                methodGenerator.addCodeLn("\", \" ");
            }
//			methodGenerator.addCode("+ " + voSimpleClassName + "." + attribute.getFieldName().toUpperCase() + " + ");
            methodGenerator.addCode("+ " + voSimpleClassName + "." + attribute.getConstName() + " + ");
            sequence = true;
        }
        methodGenerator.addCodeLn("\") \"");

        methodGenerator.addCodeLn("+ \"VALUES (\"");
        sequence = false;
        for (Attribute attribute : attributes) {
            if (sequence) {
                methodGenerator.addCodeLn(" + \", \"");
            }
            methodGenerator.addCode("+ getValueExpression(" + voVarName + "." + attribute.generateGetterMethodName() + "(), \"" + attribute.getSqlDataType() + "\")");
            sequence = true;
        }
        methodGenerator.addCodeLn(" + \")\";");

        LoggerGenerator loggerGenerator = this.getLoggerGenerator();
        methodGenerator.addCodeLn(loggerGenerator.generateDebugLogStatementByVarName("sql"));

        methodGenerator.addCodeLn("statement.addBatch(sql);");
        methodGenerator.addCodeLn("}");
        methodGenerator.addCodeLn("statement.executeBatch();");
        methodGenerator.addCodeLn("} catch (SQLException e) {");
        methodGenerator.addCodeLn("throw new " + this.connectionExceptionSimpleClassName +"(e);");
        methodGenerator.addCodeLn("} finally {");
        methodGenerator.addCodeLn("if (statement != null) { try { statement.close(); } catch (SQLException e) {}}");
//        methodGenerator.addCodeLn("releaseConnection(connection, " + this.entity.getDaoSimpleClassName() + ".class);");
//        methodGenerator.addCodeLn(this.connectionManagerSimpleClassName + ".getInstance().releaseConnection(connection, " + this.entity.getDaoSimpleClassName() + ".class);");
        methodGenerator.addCodeLn(generateReleaseConnectionStatement());
        methodGenerator.addCodeLn("}");

    }


    private void addLoadMethod() {

        String pkSimpleClassName = this.entity.getPkSimpleClassName();
        String pkVarName = this.generateVarNameFromSimpleClassName(pkSimpleClassName);

        MethodGenerator methodGenerator = this.getNewMethodGenerator();
        methodGenerator.setIsStatic(true);
        methodGenerator.setReturnTypeByCanonicalClassName(entity.getVoCanonicalClassName());
        methodGenerator.setMethodName("load");

        Attribute pkAttribute = entity.getAttributes().get(0);

        if (entity.isComposedPk()) {
            String pkCanonicalClassName = this.entity.getPkCanonicalClassName();
            methodGenerator.addAndImportParameter(pkCanonicalClassName, pkVarName);
        } else {
            methodGenerator.addParameter(pkAttribute.getJavaTypeSimpleName(), pkAttribute.getFieldName());
        }

        methodGenerator.addThrowsException(this.connectionExceptionCanonicalClassName);
        methodGenerator.addThrowsException(this.entityNotFoundExceptionCanonicalClassName);

//        methodGenerator.addCodeLn("Connection connection = openConnection(" + this.entity.getDaoSimpleClassName() + ".class" + ");");
//        methodGenerator.addCodeLn("Connection connection = " + this.connectionManagerSimpleClassName + ".getInstance().openConnection(" + this.entity.getDaoSimpleClassName() + ".class);");
        methodGenerator.addCodeLn(createGetConnectionStatement());
        methodGenerator.addCodeLn("try {");

        if (entity.isComposedPk()) {
            methodGenerator.addCodeLn("return load(" + pkVarName + ", connection);");
        } else {
            methodGenerator.addCodeLn("return load(" + pkAttribute.getFieldName() + ", connection);");
        }

        methodGenerator.addCodeLn("} catch(" + SQLException.class.getSimpleName() + " e) {");
        methodGenerator.addCodeLn("throw new " + this.connectionExceptionSimpleClassName + "(e);");
        methodGenerator.addCodeLn("} finally {");
//        methodGenerator.addCodeLn("releaseConnection(connection, " + this.entity.getDaoSimpleClassName() + ".class);");
//        methodGenerator.addCodeLn(this.connectionManagerSimpleClassName + ".getInstance().releaseConnection(connection, " + this.entity.getDaoSimpleClassName() + ".class);");
        methodGenerator.addCodeLn(generateReleaseConnectionStatement());
        methodGenerator.addCodeLn("}");
    }


    private void addLoadMethodByConnection() {

        String voSimpleClassName = this.entity.getVoSimpleClassName();
        String voVarName = this.generateVarNameFromSimpleClassName(voSimpleClassName);
        String pkSimpleClassName = this.entity.getPkSimpleClassName();
        String pkVarName = this.generateVarNameFromSimpleClassName(pkSimpleClassName);

        MethodGenerator methodGenerator = this.getNewMethodGenerator();
        methodGenerator.setIsStatic(true);
        methodGenerator.setReturnTypeByCanonicalClassName(entity.getVoCanonicalClassName());
        methodGenerator.setMethodName("load");

        Attribute pkAttribute = entity.getAttributes().get(0);

        if (entity.isComposedPk()) {
            String pkCanonicalClassName = this.entity.getPkCanonicalClassName();
            methodGenerator.addAndImportParameter(pkCanonicalClassName, pkVarName);
        } else {
            methodGenerator.addParameter(pkAttribute.getJavaTypeSimpleName(), pkAttribute.getFieldName());
        }
        methodGenerator.addAndImportParameter(Connection.class, "connection");

        methodGenerator.addThrowsException(SQLException.class);
        methodGenerator.addThrowsException(this.entityNotFoundExceptionCanonicalClassName);


        methodGenerator.addCodeLn("String sql = \"SELECT * FROM \" + " + voSimpleClassName + ".TABLENAME + \" WHERE \"");

        if (entity.isComposedPk()) {
            List<Attribute> pkAttributes = entity.getPkAttributes();
            boolean sequence = false;
            for (Attribute attribute : pkAttributes) {
                if (sequence) {
                    methodGenerator.addCodeLn(" + \" AND \"");
                }
                methodGenerator.addCode("+ " + voSimpleClassName + "." + attribute.getConstName() + " + \" = \" + getValueExpression(" + pkVarName + "." + attribute.generateGetterMethodName() + "(), \"" + attribute.getSqlDataType() + "\")");
                sequence = true;
            }
            methodGenerator.addCodeLn(";");
        } else {
            methodGenerator.addCodeLn("+ " + voSimpleClassName + "." + pkAttribute.getConstName() + " + \" = \" + getValueExpression(" + pkAttribute.getFieldName() + ", \"" + pkAttribute.getSqlDataType() + "\");");
        }

        LoggerGenerator loggerGenerator = this.getLoggerGenerator();
        methodGenerator.addCodeLn(loggerGenerator.generateDebugLogStatementByVarName("sql"));

        methodGenerator.addCodeLn("Statement statement = connection.createStatement();");
        methodGenerator.addCodeLn("ResultSet resultSet = statement.executeQuery(sql);");
        methodGenerator.addCodeLn("try {");
        methodGenerator.addCodeLn("if (resultSet.next()) {");

        methodGenerator.addCode(voSimpleClassName + " " + voVarName + " = new " + voSimpleClassName + "(");
        if (entity.isComposedPk()) {
            methodGenerator.addCode(pkVarName);
        } else {
            methodGenerator.addCode(pkAttribute.getFieldName());
        }
        methodGenerator.addCodeLn(");");

        List<Attribute> nonPkAttributes = entity.getNonPkAttributes();
        for (Attribute attribute : nonPkAttributes) {
            methodGenerator.addCodeLn(generateLocalVarFromResultSet(entity, attribute));
            methodGenerator.addCodeLn(voVarName + "." + attribute.generateSetterMethodName() + "(" + attribute.getFieldName() + ");");
        }

        methodGenerator.addCodeLn("return " + voVarName + ";");
        methodGenerator.addCodeLn("} else {");

        methodGenerator.addCode("throw new " + this.entityNotFoundExceptionSimpleClassName
                + "(" + voSimpleClassName + ".TABLENAME + \"-Entity with primary key ");
        if (entity.isComposedPk()) {
            methodGenerator.addCode(pkSimpleClassName + " = \" + " + pkVarName + ".toString()");
        } else {
            methodGenerator.addCode("field '" + pkAttribute.getFieldName() + "' = \" + " + pkAttribute.getFieldName());
        }
        methodGenerator.addCodeLn(" + \" does not exist!\");");
        methodGenerator.addCodeLn("}");
        methodGenerator.addCodeLn("} finally {");
        methodGenerator.addCodeLn("if (resultSet != null) { try { resultSet.close(); } catch (SQLException e) {}}");
        methodGenerator.addCodeLn("if (statement != null) { try { statement.close(); } catch (SQLException e) {}}");
        methodGenerator.addCodeLn("}");
    }

    private void addDeleteMethod() {

        String pkSimpleClassName = this.entity.getPkSimpleClassName();
        String pkVarName = generateVarNameFromSimpleClassName(pkSimpleClassName);
        Attribute pkAttribute = this.entity.getPkAttributes().get(0);

        MethodGenerator methodGenerator = this.getNewMethodGenerator();
        methodGenerator.setIsStatic(true);
        methodGenerator.setMethodName("delete");

        if (this.entity.isComposedPk()) {
            methodGenerator.addAndImportParameter(this.entity.getPkCanonicalClassName(), pkVarName);
        } else {
            methodGenerator.addParameter(pkAttribute.getJavaTypeSimpleName(), pkAttribute.getFieldName());
        }

        methodGenerator.addThrowsException(this.connectionExceptionCanonicalClassName);

//        methodGenerator.addCodeLn("Connection connection = openConnection(" + this.entity.getDaoSimpleClassName() + ".class" + ");");
//        methodGenerator.addCodeLn("Connection connection = " + this.connectionManagerSimpleClassName + ".getInstance().openConnection(" + this.entity.getDaoSimpleClassName() + ".class);");
        methodGenerator.addCodeLn(createGetConnectionStatement());
        methodGenerator.addCodeLn("try {");

        if (this.entity.isComposedPk()) {
            methodGenerator.addCodeLn("delete(" + pkVarName + ", connection);");
        } else {
            methodGenerator.addCodeLn("delete(" + pkAttribute.getFieldName() + ", connection);");
        }

        methodGenerator.addCodeLn("} catch (" + SQLException.class.getSimpleName() + " e) {");
        methodGenerator.addCodeLn("throw new " + this.connectionExceptionSimpleClassName + "(e);");
        methodGenerator.addCodeLn("} finally {");
//        methodGenerator.addCodeLn("releaseConnection(connection, " + this.entity.getDaoSimpleClassName() + ".class);");
//        methodGenerator.addCodeLn(this.connectionManagerSimpleClassName + ".getInstance().releaseConnection(connection, " + this.entity.getDaoSimpleClassName() + ".class);");
        methodGenerator.addCodeLn(generateReleaseConnectionStatement());
        methodGenerator.addCodeLn("}");
    }

    private void addDeleteMethodByConnection() {

        String pkSimpleClassName = this.entity.getPkSimpleClassName();
        String pkVarName = generateVarNameFromSimpleClassName(pkSimpleClassName);
        Attribute pkAttribute = this.entity.getPkAttributes().get(0);
        String voSimpleClassName = this.entity.getVoSimpleClassName();

        MethodGenerator methodGenerator = this.getNewMethodGenerator();
        methodGenerator.setIsStatic(true);
        methodGenerator.setMethodName("delete");

        if (this.entity.isComposedPk()) {
            methodGenerator.addAndImportParameter(this.entity.getPkCanonicalClassName(), pkVarName);
        } else {
            methodGenerator.addParameter(pkAttribute.getJavaTypeSimpleName(), pkAttribute.getFieldName());
        }
        methodGenerator.addAndImportParameter(Connection.class, "connection");

        methodGenerator.addThrowsException(SQLException.class);

        methodGenerator.addCodeLn("String sql = \"DELETE FROM \" + " + voSimpleClassName + ".TABLENAME + \" WHERE \"");

        if (this.entity.isComposedPk()) {
            List<Attribute> pkAttributes = this.entity.getPkAttributes();
            boolean sequence = false;
            for (Attribute attribute : pkAttributes) {
                if (sequence) {
                    methodGenerator.addCodeLn(" + \" AND \"");
                }
                methodGenerator.addCode("+ " + voSimpleClassName + "." + attribute.getConstName() + " + \" = \" + getValueExpression(" + pkVarName + "." + attribute.generateGetterMethodName() + "(), \"" + attribute.getSqlDataType() + "\")");
                sequence = true;
            }
            methodGenerator.addCodeLn(";");
        } else {
            methodGenerator.addCodeLn("+ " + voSimpleClassName + "." + pkAttribute.getConstName() + " + \" = \" + getValueExpression(" + pkAttribute.getFieldName() + ", \"" + pkAttribute.getSqlDataType() + "\");");
        }

        LoggerGenerator loggerGenerator = this.getLoggerGenerator();
        methodGenerator.addCodeLn(loggerGenerator.generateDebugLogStatementByVarName("sql"));

        methodGenerator.addCodeLn("Statement statement = connection.createStatement();");
        methodGenerator.addCodeLn("statement.execute(sql);");
        methodGenerator.addCodeLn("if (statement != null) { try { statement.close(); } catch (SQLException e) {}}");
    }

    private void addUpdateMethod() {

        String voSimpleClassName = this.entity.getVoSimpleClassName();
        String voCanonicalClassName = this.entity.getVoCanonicalClassName();
        String voVarName = generateVarNameFromSimpleClassName(voSimpleClassName);

        MethodGenerator methodGenerator = this.getNewMethodGenerator();
        methodGenerator.setIsStatic(true);
        methodGenerator.setMethodName("update");
        methodGenerator.addAndImportParameter(voCanonicalClassName, voVarName);
        methodGenerator.addThrowsException(this.connectionExceptionCanonicalClassName);

//        methodGenerator.addCodeLn("Connection connection = openConnection(" + this.entity.getDaoSimpleClassName() + ".class" + ");");
//        methodGenerator.addCodeLn("Connection connection = " + this.connectionManagerSimpleClassName + ".getInstance().openConnection(" + this.entity.getDaoSimpleClassName() + ".class);");
        methodGenerator.addCodeLn(createGetConnectionStatement());
        methodGenerator.addCodeLn("try {");

        methodGenerator.addCodeLn("update(" + voVarName + ", connection);");

        methodGenerator.addCodeLn("} catch (" + SQLException.class.getSimpleName() + " e) {");
        methodGenerator.addCodeLn("throw new " + this.connectionExceptionSimpleClassName + "(e);");
        methodGenerator.addCodeLn("} finally {");
//        methodGenerator.addCodeLn("releaseConnection(connection, " + this.entity.getDaoSimpleClassName() + ".class);");
//        methodGenerator.addCodeLn(this.connectionManagerSimpleClassName + ".getInstance().releaseConnection(connection, " + this.entity.getDaoSimpleClassName() + ".class);");
        methodGenerator.addCodeLn(generateReleaseConnectionStatement());
        methodGenerator.addCodeLn("}");
    }


    private void addUpdateMethodByConnection() {

        String voSimpleClassName = this.entity.getVoSimpleClassName();
        String voCanonicalClassName = this.entity.getVoCanonicalClassName();
        String voVarName = generateVarNameFromSimpleClassName(voSimpleClassName);

        MethodGenerator methodGenerator = this.getNewMethodGenerator();
        methodGenerator.setIsStatic(true);
        methodGenerator.setMethodName("update");
        methodGenerator.addAndImportParameter(voCanonicalClassName, voVarName);
        methodGenerator.addAndImportParameter(Connection.class, "connection");
        methodGenerator.addThrowsException(SQLException.class);

        methodGenerator.addCodeLn("String sql = \"UPDATE \" + " + voSimpleClassName + ".TABLENAME + \" SET \"");

        List<Attribute> nonPkAttributes = this.entity.getNonPkAttributes();
        boolean sequence = false;
        for (Attribute attribute : nonPkAttributes) {
            if (sequence) {
                methodGenerator.addCodeLn(" + \", \"");
            }
            methodGenerator.addCode("+ " + voSimpleClassName + "." + attribute.getConstName() + " + \" = \" + getValueExpression(" + voVarName + "." + attribute.generateGetterMethodName() + "(), \"" + attribute.getSqlDataType() + "\")");
            sequence = true;
        }
        methodGenerator.addCodeLn("");
        methodGenerator.addCodeLn("+ \" WHERE \"");

        List<Attribute> pkAttributes = this.entity.getPkAttributes();
        sequence = false;
        for (Attribute attribute : pkAttributes) {
            if (sequence) {
                methodGenerator.addCodeLn(" + \" AND \"");
            }
            methodGenerator.addCode("+ " + voSimpleClassName + "." + attribute.getConstName() + " + \" = \" + getValueExpression(" + voVarName + "." + attribute.generateGetterMethodName() + "(), \"" + attribute.getSqlDataType() + "\")");
            sequence = true;
        }
        methodGenerator.addCodeLn(";");

        LoggerGenerator loggerGenerator = this.getLoggerGenerator();
        methodGenerator.addCodeLn(loggerGenerator.generateDebugLogStatementByVarName("sql"));

        methodGenerator.addCodeLn("Statement statement = connection.createStatement();");
        methodGenerator.addCodeLn("statement.execute(sql);");
        methodGenerator.addCodeLn("if (statement != null) { try { statement.close(); } catch (SQLException e) {}}");
    }


    private void addFindAllMethod() {

        String voSimpleClassName = this.entity.getVoSimpleClassName();
        String voVarName = generateVarNameFromSimpleClassName(voSimpleClassName);
        String voListVarName = voVarName + "List";
        String pkSimpleClassName = this.entity.getPkSimpleClassName();
        String pkVarName = generateVarNameFromSimpleClassName(pkSimpleClassName);

        MethodGenerator methodGenerator = this.getNewMethodGenerator();
        methodGenerator.setIsStatic(true);
        methodGenerator.setReturnType(List.class);
        methodGenerator.setReturnTypeParameter(this.entity.getVoCanonicalClassName());
        methodGenerator.setMethodName("findAll");
        methodGenerator.addThrowsException(this.connectionExceptionCanonicalClassName);

//        methodGenerator.addCodeLn("Connection connection = openConnection(" + this.entity.getDaoSimpleClassName() + ".class" + ");");
//        methodGenerator.addCodeLn("Connection connection = " + this.connectionManagerSimpleClassName + ".getInstance().openConnection(" + this.entity.getDaoSimpleClassName() + ".class);");
        methodGenerator.addCodeLn(createGetConnectionStatement());
        methodGenerator.addCodeLn("Statement statement = null;");
        methodGenerator.addCodeLn("ResultSet resultSet = null;");
        methodGenerator.addCodeLn("try {");

        methodGenerator.addCodeLn("statement = connection.createStatement();");
        methodGenerator.addCodeLn("String sql = \"SELECT * FROM \" + " + voSimpleClassName + ".TABLENAME;");

        LoggerGenerator loggerGenerator = this.getLoggerGenerator();
        methodGenerator.addCodeLn(loggerGenerator.generateDebugLogStatementByVarName("sql"));

        methodGenerator.addCodeLn("resultSet = statement.executeQuery(sql);");

        methodGenerator.addCodeLn("List<" + voSimpleClassName + "> " + voListVarName + " = new ArrayList<" + voSimpleClassName + ">();");
        methodGenerator.addCodeLn("while (resultSet.next()) {");

        List<Attribute> attributes = this.entity.getAttributes();
        for (Attribute attribute : attributes) {
            methodGenerator.addCodeLn(generateLocalVarFromResultSet(this.entity, attribute));
        }

        if (this.entity.isComposedPk()) {
            methodGenerator.addCode(pkSimpleClassName + " " + pkVarName + " = new " + pkSimpleClassName + "(");
            List<Attribute> pkAttributes = this.entity.getPkAttributes();
            boolean sequence = false;
            for (Attribute attribute : pkAttributes) {
                if (sequence) {
                    methodGenerator.addCode(", ");
                }
                methodGenerator.addCode(attribute.getFieldName());
                sequence = true;
            }
            methodGenerator.addCodeLn(");");
            methodGenerator.addCodeLn(voSimpleClassName + " " + voVarName + " = new " + voSimpleClassName + "(" + pkVarName + ");");

        } else {
            Attribute pkAttribute = this.entity.getPkAttributes().get(0);

            methodGenerator.addCodeLn(voSimpleClassName + " " + voVarName + " = new " + voSimpleClassName + "(" + pkAttribute.getFieldName() + ");");
        }

        List<Attribute> nonPkAttributes = this.entity.getNonPkAttributes();
        for (Attribute attribute : nonPkAttributes) {
            methodGenerator.addCodeLn(voVarName + "." + attribute.generateSetterMethodName() + "(" + attribute.getFieldName() + ");");
        }

        methodGenerator.addCodeLn(voListVarName + ".add(" + voVarName + ");");
        methodGenerator.addCodeLn("}");
        methodGenerator.addCodeLn("return " + voListVarName + ";");
        methodGenerator.addCodeLn("} catch (" + SQLException.class.getSimpleName() + " e) {");
        methodGenerator.addCodeLn("throw new " + this.connectionExceptionSimpleClassName + "(e);");
        methodGenerator.addCodeLn("} finally {");
        methodGenerator.addCodeLn("if (resultSet != null) { try { resultSet.close(); } catch (SQLException e) {}}");
        methodGenerator.addCodeLn("if (statement != null) { try { statement.close(); } catch (SQLException e) {}}");
//        methodGenerator.addCodeLn("releaseConnection(connection, " + this.entity.getDaoSimpleClassName() + ".class);");
//        methodGenerator.addCodeLn(this.connectionManagerSimpleClassName + ".getInstance().releaseConnection(connection, " + this.entity.getDaoSimpleClassName() + ".class);");
        methodGenerator.addCodeLn(generateReleaseConnectionStatement());
        methodGenerator.addCodeLn("}");
    }

    private void addFindByForeignKeyMethod(ForeignKeyWrapper foreignKeyWrapper) {

        List<Attribute> fkAttributes = foreignKeyWrapper.getKeyFieldAttributes();

        String foreignKeyName = foreignKeyWrapper.getForeignKeyName();
        String voSimpleClassName = this.entity.getVoSimpleClassName();
        String voVarName = generateVarNameFromSimpleClassName(voSimpleClassName);
        String pkSimpleClassName = this.entity.getPkSimpleClassName();
        String pkVarName = generateVarNameFromSimpleClassName(pkSimpleClassName);
        String voListVarName = generateVarNameFromSimpleClassName(voSimpleClassName) + "List";

        MethodGenerator methodGenerator = this.getNewMethodGenerator();
        methodGenerator.setIsStatic(true);
        methodGenerator.setReturnType(List.class);
        methodGenerator.setReturnTypeParameter(this.entity.getVoCanonicalClassName());
        methodGenerator.setMethodName("findBy" + Helper.shiftCaseFirstLetter(foreignKeyName));
        for (Attribute fkAttribute : fkAttributes) {
            methodGenerator.addParameter(fkAttribute.getJavaTypeSimpleName(), fkAttribute.getFieldName());
        }
        methodGenerator.addThrowsException(this.connectionExceptionCanonicalClassName);

//        methodGenerator.addCodeLn("Connection connection = openConnection(" + this.entity.getDaoSimpleClassName() + ".class" + ");");
//        methodGenerator.addCodeLn("Connection connection = " + this.connectionManagerSimpleClassName + ".getInstance().openConnection(" + this.entity.getDaoSimpleClassName() + ".class);");
        methodGenerator.addCodeLn(createGetConnectionStatement());
        methodGenerator.addCodeLn("Statement statement = null;");
        methodGenerator.addCodeLn("ResultSet resultSet = null;");
        methodGenerator.addCodeLn("try {");
        methodGenerator.addCodeLn("statement = connection.createStatement();");
        methodGenerator.addCodeLn("String sql = \"SELECT * FROM \" + " + voSimpleClassName + ".TABLENAME + \" WHERE \"");

        boolean sequence = false;
        for (int i=0; i<fkAttributes.size(); i++) {
            Attribute fkAttribute = fkAttributes.get(i);
            if (sequence) {
                methodGenerator.addCodeLn(" + \" AND \"");
            }
//			methodGenerator.addCode("+ " + voSimpleClassName + "." + fkAttribute.getFieldName().toUpperCase()
//					+ " + \" = \" + getValueExpression(" + fkAttribute.getFieldName() + ", \"" + fkAttribute.getSqlDataType() + "\")");			
            methodGenerator.addCode("+ " + voSimpleClassName + "." + fkAttribute.getConstName()
                    + " + \" = \" + getValueExpression(" + fkAttribute.getFieldName() + ", \"" + fkAttribute.getSqlDataType() + "\")");
            sequence = true;
        }
        methodGenerator.addCodeLn(";");

        LoggerGenerator loggerGenerator = this.getLoggerGenerator();
        methodGenerator.addCodeLn(loggerGenerator.generateDebugLogStatementByVarName("sql"));

        methodGenerator.addCodeLn("resultSet = statement.executeQuery(sql);");
        methodGenerator.addCodeLn("List<" + voSimpleClassName + "> " + voListVarName + " = new ArrayList<" + voSimpleClassName + ">();");

        methodGenerator.addCodeLn("while (resultSet.next()) {");

        // Make for every entity-attribute an assignment from result set to local var
        // except attributes, that were passed to this method.
        List<Attribute> attributes = this.entity.getAttributes();
        for (Attribute attribute : attributes) {
            boolean exceptAttribute = false;
            for (Attribute fkAttribute : fkAttributes) {
                if (fkAttribute.equals(attribute)) {
                    exceptAttribute = true;
                    break;
                }
            }
            if (!exceptAttribute) {
                methodGenerator.addCodeLn(generateLocalVarFromResultSet(this.entity, attribute));
            }
        }

        if (this.entity.isComposedPk()) {
            methodGenerator.addCode(pkSimpleClassName + " " + pkVarName + " = new " + pkSimpleClassName + "(");
            List<Attribute> pkAttributes = this.entity.getPkAttributes();
            sequence = false;
            for (Attribute attribute : pkAttributes) {
                if (sequence) {
                    methodGenerator.addCode(", ");
                }
                methodGenerator.addCode(attribute.getFieldName());
                sequence = true;
            }
            methodGenerator.addCodeLn(");");
            methodGenerator.addCodeLn(voSimpleClassName + " " + voVarName + " = new " + voSimpleClassName + "(" + pkVarName + ");");

        } else {
            Attribute pkAttribute = this.entity.getPkAttributes().get(0);

            methodGenerator.addCodeLn(voSimpleClassName + " " + voVarName + " = new " + voSimpleClassName + "(" + pkAttribute.getFieldName() + ");");
        }

        List<Attribute> nonPkAttributes = this.entity.getNonPkAttributes();
        for (Attribute attribute : nonPkAttributes) {
            methodGenerator.addCodeLn(voVarName + "." + attribute.generateSetterMethodName() + "(" + attribute.getFieldName() + ");");
        }

        methodGenerator.addCodeLn(voListVarName + ".add(" + voVarName + ");");

        methodGenerator.addCodeLn("}");
        methodGenerator.addCodeLn("return " + voListVarName + ";");
        methodGenerator.addCodeLn("} catch (SQLException e) {");
        methodGenerator.addCodeLn("throw new " + this.connectionExceptionSimpleClassName + "(e);");
        methodGenerator.addCodeLn("} finally {");
        methodGenerator.addCodeLn("if (resultSet != null) { try { resultSet.close(); } catch (SQLException e) {}}");
        methodGenerator.addCodeLn("if (statement != null) { try { statement.close(); } catch (SQLException e) {}}");

//        methodGenerator.addCodeLn("releaseConnection(connection, " + this.entity.getDaoSimpleClassName() + ".class);");
//        methodGenerator.addCodeLn(this.connectionManagerSimpleClassName + ".getInstance().releaseConnection(connection, " + this.entity.getDaoSimpleClassName() + ".class);");
        methodGenerator.addCodeLn(generateReleaseConnectionStatement());
        methodGenerator.addCodeLn("}");
    }

    private void addFindByUniqueKeyMethod(String uniqueKeyName, List<Attribute> uniqueKeyAttributeList) {

        String voSimpleClassName = this.entity.getVoSimpleClassName();
        String voVarName = generateVarNameFromSimpleClassName(voSimpleClassName);
        String pkSimpleClassName = this.entity.getPkSimpleClassName();
        String pkVarName = generateVarNameFromSimpleClassName(pkSimpleClassName);

        MethodGenerator methodGenerator = this.getNewMethodGenerator();
        methodGenerator.setIsStatic(true);
        methodGenerator.setReturnTypeByCanonicalClassName(this.entity.getVoCanonicalClassName());
//		methodGenerator.setReturnTypeParameter(this.entity.getVoCanonicalClassName());
        methodGenerator.setMethodName("findBy" + Helper.shiftCaseFirstLetter(uniqueKeyName));
        for (Attribute uniqueKeyAttribute : uniqueKeyAttributeList) {
            methodGenerator.addParameter(uniqueKeyAttribute.getJavaTypeSimpleName(), uniqueKeyAttribute.getFieldName());
        }
        methodGenerator.addThrowsException(this.connectionExceptionCanonicalClassName);
        methodGenerator.addThrowsException(this.entityNotFoundExceptionCanonicalClassName);

//        methodGenerator.addCodeLn("Connection connection = openConnection(" + this.entity.getDaoSimpleClassName() + ".class" + ");");
//        methodGenerator.addCodeLn("Connection connection = " + this.connectionManagerSimpleClassName + ".getInstance().openConnection(" + this.entity.getDaoSimpleClassName() + ".class);");
        methodGenerator.addCodeLn(createGetConnectionStatement());
        methodGenerator.addCodeLn("Statement statement = null;");
        methodGenerator.addCodeLn("ResultSet resultSet = null;");
        methodGenerator.addCodeLn("try {");
        methodGenerator.addCodeLn("statement = connection.createStatement();");
        methodGenerator.addCodeLn("String sql = \"SELECT * FROM \" + " + voSimpleClassName + ".TABLENAME + \" WHERE \"");

        boolean sequence = false;
        for (Attribute attribute : uniqueKeyAttributeList) {
            if (sequence) {
                methodGenerator.addCodeLn(" + \" AND \"");
            }
            methodGenerator.addCode("+ " + voSimpleClassName + "." + attribute.getConstName() + " + \" = \" + getValueExpression(" + attribute.getFieldName() + ", \"" + attribute.getSqlDataType() + "\")");
            sequence = true;
        }
        methodGenerator.addCodeLn(";");

        LoggerGenerator loggerGenerator = this.getLoggerGenerator();
        methodGenerator.addCodeLn(loggerGenerator.generateDebugLogStatementByVarName("sql"));

        methodGenerator.addCodeLn("resultSet = statement.executeQuery(sql);");
        methodGenerator.addCodeLn("if (resultSet.next()) {");

        List<Attribute> attributes = this.entity.getAttributes();
        for (Attribute attribute : attributes) {
            if (!uniqueKeyAttributeList.contains(attribute)) {
                methodGenerator.addCodeLn(generateLocalVarFromResultSet(this.entity, attribute));
            }
        }

        if (this.entity.isComposedPk()) {

            methodGenerator.addCode(pkSimpleClassName + " " + pkVarName + " = new " + pkSimpleClassName + "(");
            List<Attribute> pkAttributes = this.entity.getPkAttributes();
            sequence = false;
            for (Attribute attribute : pkAttributes) {
                if (sequence) {
                    methodGenerator.addCode(", ");
                }
                methodGenerator.addCode(attribute.getFieldName());
                sequence = true;
            }
            methodGenerator.addCodeLn(");");
            methodGenerator.addCodeLn(voSimpleClassName + " " + voVarName + " = new " + voSimpleClassName + "(" + pkVarName + ");");

        } else {
            Attribute pkAttribute = this.entity.getPkAttributes().get(0);

            methodGenerator.addCodeLn(voSimpleClassName + " " + voVarName + " = new " + voSimpleClassName + "(" + pkAttribute.getFieldName() + ");");
        }

        List<Attribute> nonPkAttributes = this.entity.getNonPkAttributes();
        for (Attribute attribute : nonPkAttributes) {
            methodGenerator.addCodeLn(voVarName + "." + attribute.generateSetterMethodName() + "(" + attribute.getFieldName() + ");");
        }

        methodGenerator.addCodeLn("return " + voVarName + ";");
        methodGenerator.addCodeLn("} else {");

        methodGenerator.addCode("throw new " + this.entityNotFoundExceptionSimpleClassName
                + "(" + voSimpleClassName + ".TABLENAME + \"-Entity with unique key '" + uniqueKeyName + "'[");
        sequence = false;
        for (Attribute attribute : uniqueKeyAttributeList) {
            if (sequence) {
                methodGenerator.addCode(" + \", ");
            }
            methodGenerator.addCode(attribute.getFieldName() + " = \" + " + attribute.getFieldName());
            sequence = true;
        }

        methodGenerator.addCodeLn(" + \"] does not exist!\");");
        methodGenerator.addCodeLn("}");
        methodGenerator.addCodeLn("} catch(" + SQLException.class.getSimpleName() + " e) {");
        methodGenerator.addCodeLn("throw new " + this.connectionExceptionSimpleClassName + "(e);");
        methodGenerator.addCodeLn("} finally {");
        methodGenerator.addCodeLn("if (resultSet != null) { try { resultSet.close(); } catch (SQLException e) {}}");
        methodGenerator.addCodeLn("if (statement != null) { try { statement.close(); } catch (SQLException e) {}}");
//        methodGenerator.addCodeLn("releaseConnection(connection, " + this.entity.getDaoSimpleClassName() + ".class);");
//        methodGenerator.addCodeLn(this.connectionManagerSimpleClassName + ".getInstance().releaseConnection(connection, " + this.entity.getDaoSimpleClassName() + ".class);");
        methodGenerator.addCodeLn(generateReleaseConnectionStatement());
        methodGenerator.addCodeLn("}");

    }

    private void addGetAssociatedEntites(ForeignKeyWrapper foreignKeyWrapperA, ForeignKeyWrapper foreignKeyWrapperB) {

        Entity sourceEntity = foreignKeyWrapperA.getTargetEntity();
        Entity targetEntity = foreignKeyWrapperB.getTargetEntity();
        String sourceEntityVoCanonicalClassName = sourceEntity.getVoCanonicalClassName();
        String sourceEntityVoSimpleClassName = sourceEntity.getVoSimpleClassName();
        String targetEntityVoCanonicalClassName = targetEntity.getVoCanonicalClassName();
        String targetEntityVoSimpleClassName = targetEntity.getVoSimpleClassName();
        String associationEntityVoSimpleClassName = this.entity.getVoSimpleClassName();
        String voListVarName = generateVarNameFromSimpleClassName(targetEntityVoSimpleClassName) + "List";
        String pkSimpleClassName = foreignKeyWrapperB.getTargetEntity().getPkSimpleClassName();
        String pkCanonicalClassName = foreignKeyWrapperB.getTargetEntity().getPkCanonicalClassName();
        String pkVarName = generateVarNameFromSimpleClassName(pkSimpleClassName);
        String voVarName = generateVarNameFromSimpleClassName(targetEntityVoSimpleClassName);

        MethodGenerator methodGenerator = this.getNewMethodGenerator();
        methodGenerator.setIsStatic(true);
        methodGenerator.setReturnType(List.class);
        methodGenerator.setReturnTypeParameter(targetEntityVoCanonicalClassName);

        methodGenerator.setMethodName("getAssociated" + targetEntityVoSimpleClassName);

        String sourceEntityVoVarName = generateVarNameFromSimpleClassName(sourceEntityVoSimpleClassName);
        methodGenerator.addAndImportParameter(sourceEntityVoCanonicalClassName, sourceEntityVoVarName);

        methodGenerator.addThrowsException(this.connectionExceptionCanonicalClassName);

        List<Attribute> targetEntityAttributes = foreignKeyWrapperB.getTargetEntity().getAttributes();
        List<Attribute> targetFkTargetAttributes = foreignKeyWrapperB.getTargetFieldAttributes();
        List<Attribute> targetFkKeyAttributes = foreignKeyWrapperB.getKeyFieldAttributes();

        methodGenerator.addCode("String sql = \"SELECT ");
        boolean sequence = false;

        for (int i=0; i<targetEntityAttributes.size(); i++) {
            Attribute attribute = targetEntityAttributes.get(i);
            if (sequence) {
                methodGenerator.addCode(" + \", ");
            }
            methodGenerator.addCode("t.\" + " + targetEntityVoSimpleClassName + "." + attribute.getConstName());
//					+ " + \" = \" + getValueExpression(" + fkAttribute.getFieldName() + ", \"" + fkAttribute.getSqlDataType() + "\")");			
            sequence = true;
        }
        methodGenerator.addLn();

        methodGenerator.addCodeLn("+ \" FROM \" + "
                + targetEntityVoSimpleClassName + ".TABLENAME + \" AS t, \" + "
                + associationEntityVoSimpleClassName + ".TABLENAME + \" AS a\"");

        methodGenerator.addCode("+ \" WHERE \" ");
        sequence = false;
        for (int i=0; i<targetFkKeyAttributes.size(); i++) {
            Attribute keyAttribute = targetFkKeyAttributes.get(i);
            Attribute targetAttribute = targetFkTargetAttributes.get(i);
            if (sequence) {
                methodGenerator.addCodeLn(" + \" AND \" ");
            }
            methodGenerator.addCode("+ \"a.\" + " + associationEntityVoSimpleClassName + "." + keyAttribute.getConstName()
                    + " + \" = \" + \"t.\" + " + targetEntityVoSimpleClassName + "." + targetAttribute.getConstName());
            sequence = true;
        }

        List<Attribute> sourceFkTargetAttributes = foreignKeyWrapperA.getTargetFieldAttributes();
        List<Attribute> sourceFkKeyAttributes = foreignKeyWrapperA.getKeyFieldAttributes();

        methodGenerator.addCodeLn(" + \" AND \" ");
        sequence = false;
        for (int i=0; i<sourceFkKeyAttributes.size(); i++) {
            Attribute keyAttribute = sourceFkKeyAttributes.get(i);
            Attribute targetAttribute = sourceFkTargetAttributes.get(i);
            if (sequence) {
                methodGenerator.addCodeLn(" + \" AND \" ");
            }
            methodGenerator.addCode("+ \"a.\" + " + associationEntityVoSimpleClassName + "." + keyAttribute.getConstName()
                    + " + \" = \" + getValueExpression(" + sourceEntityVoVarName + "." + targetAttribute.generateGetterMethodName() + "(), \"" + targetAttribute.getSqlDataType() + "\")");
            sequence = true;
        }

        methodGenerator.addCodeLn(";");

        LoggerGenerator loggerGenerator = this.getLoggerGenerator();
        methodGenerator.addCodeLn(loggerGenerator.generateDebugLogStatementByVarName("sql"));

//        methodGenerator.addCodeLn("Connection connection = openConnection(" + this.entity.getDaoSimpleClassName() + ".class" + ");");
//        methodGenerator.addCodeLn("Connection connection = " + this.connectionManagerSimpleClassName + ".getInstance().openConnection(" + this.entity.getDaoSimpleClassName() + ".class);");
        methodGenerator.addCodeLn(createGetConnectionStatement());
        methodGenerator.addCodeLn("Statement statement = null;");
        methodGenerator.addCodeLn("ResultSet resultSet = null;");
        methodGenerator.addCodeLn("try {");
        methodGenerator.addCodeLn("statement = connection.createStatement();");
        methodGenerator.addCodeLn("resultSet = statement.executeQuery(sql);");

        methodGenerator.addCodeLn("List<" + targetEntityVoSimpleClassName + "> " + voListVarName + " = new ArrayList<" + targetEntityVoSimpleClassName + ">();");

        methodGenerator.addCodeLn("while (resultSet.next()) {");

        // Make for every entity-attribute an assignment from result set to local var
        List<Attribute> attributes = targetEntity.getAttributes();
        for (Attribute attribute : attributes) {
            methodGenerator.addCodeLn(generateLocalVarFromResultSet(targetEntity, attribute));
        }

        if (targetEntity.isComposedPk()) {
            methodGenerator.addImport(pkCanonicalClassName);
            methodGenerator.addCode(pkSimpleClassName + " " + pkVarName + " = new " + pkSimpleClassName + "(");
            List<Attribute> pkAttributes = targetEntity.getPkAttributes();
            sequence = false;
            for (Attribute attribute : pkAttributes) {
                if (sequence) {
                    methodGenerator.addCode(", ");
                }
                methodGenerator.addCode(attribute.getFieldName());
                sequence = true;
            }
            methodGenerator.addCodeLn(");");
            methodGenerator.addCodeLn(targetEntityVoSimpleClassName + " " + voVarName + " = new " + targetEntityVoSimpleClassName + "(" + pkVarName + ");");

        } else {
            Attribute pkAttribute = targetEntity.getPkAttributes().get(0);

            methodGenerator.addCodeLn(targetEntityVoSimpleClassName + " " + voVarName + " = new " + targetEntityVoSimpleClassName + "(" + pkAttribute.getFieldName() + ");");
        }

        List<Attribute> nonPkAttributes = targetEntity.getNonPkAttributes();
        for (Attribute attribute : nonPkAttributes) {
            methodGenerator.addCodeLn(voVarName + "." + attribute.generateSetterMethodName() + "(" + attribute.getFieldName() + ");");
        }

        methodGenerator.addCodeLn(voListVarName + ".add(" + voVarName + ");");

        methodGenerator.addCodeLn("}");
        methodGenerator.addCodeLn("return " + voListVarName + ";");


        methodGenerator.addCodeLn("} catch (SQLException e) {");
        methodGenerator.addCodeLn("throw new " + this.connectionExceptionSimpleClassName + "(e);");
        methodGenerator.addCodeLn("} finally {");
        methodGenerator.addCodeLn("if (resultSet != null) { try { resultSet.close(); } catch (SQLException e) {}}");
        methodGenerator.addCodeLn("if (statement != null) { try { statement.close(); } catch (SQLException e) {}}");
//        methodGenerator.addCodeLn("releaseConnection(connection, " + this.entity.getDaoSimpleClassName() + ".class);");
//        methodGenerator.addCodeLn(this.connectionManagerSimpleClassName + ".getInstance().releaseConnection(connection, " + this.entity.getDaoSimpleClassName() + ".class);");
        methodGenerator.addCodeLn(generateReleaseConnectionStatement());
        methodGenerator.addCodeLn("}");

    }

    private void addAssociation(ForeignKeyWrapper foreignKeyWrapperA, ForeignKeyWrapper foreignKeyWrapperB) {

        Entity entityA = foreignKeyWrapperA.getTargetEntity();
        Entity entityB = foreignKeyWrapperB.getTargetEntity();

        MethodGenerator methodGenerator = this.getNewMethodGenerator();
        methodGenerator.setIsStatic(true);
//		methodGenerator.setReturnType(List.class);

        methodGenerator.setMethodName("addAssociation");

        String entityAVoVarName = generateVarNameFromSimpleClassName(entityA.getVoSimpleClassName());
        String entityBVoVarName = generateVarNameFromSimpleClassName(entityB.getVoSimpleClassName());
        methodGenerator.addAndImportParameter(entityA.getVoCanonicalClassName(), entityAVoVarName);
        methodGenerator.addAndImportParameter(entityB.getVoCanonicalClassName(), entityBVoVarName);

        methodGenerator.addThrowsException(this.connectionExceptionCanonicalClassName);

        String pkClassName = this.entity.getPkSimpleClassName();
        String pkVarName = this.generateVarNameFromSimpleClassName(pkClassName);
        methodGenerator.addCode(pkClassName + " " + pkVarName + " = new " + pkClassName + "(");

        List<Attribute> pkAttributes = this.entity.getPkAttributes();
        boolean sequence = false;
        for (Attribute pkAttribute : pkAttributes) {
            if (sequence) {
                methodGenerator.addCode(", ");
            }
            Attribute referencedAttribute = foreignKeyWrapperA.getReferencedFieldAttributeByReferencingFieldAttribute(pkAttribute);
            if (referencedAttribute != null) {
                methodGenerator.addCode(entityAVoVarName + "." + referencedAttribute.generateGetterMethodName() + "()");
            } else {
                referencedAttribute = foreignKeyWrapperB.getReferencedFieldAttributeByReferencingFieldAttribute(pkAttribute);
                methodGenerator.addCode(entityBVoVarName + "." + referencedAttribute.generateGetterMethodName() + "()");
            }
            sequence = true;
        }

        methodGenerator.addCodeLn(");");

        String voClassName = this.entity.getVoSimpleClassName();
        String voVarName = this.generateVarNameFromSimpleClassName(voClassName);

        methodGenerator.addCodeLn(voClassName + " " + voVarName + " = new " + voClassName + "(" + pkVarName + ");");
        methodGenerator.addCodeLn(this.entity.getDaoSimpleClassName() + ".create(" + voVarName + ");");
    }

    private void addGetValueExpressionMethod() {

        MethodGenerator methodGenerator = this.getNewMethodGenerator();

        methodGenerator.setAccessibility(MethodGenerator.Accessibility.PRIVATE);
        methodGenerator.setIsStatic(true);
        methodGenerator.setReturnType(String.class);

        methodGenerator.setMethodName("getValueExpression");
        methodGenerator.addParameter("Object", "o");
        methodGenerator.addParameter("String", "sqlType");

        methodGenerator.addCodeLn("if (o == null) { return \"NULL\"; }");
        methodGenerator.addCodeLn("if (sqlType.startsWith(\"VARCHAR\") || sqlType.equals(\"DATE\")) { return \"'\" + o + \"'\"; }");
        methodGenerator.addCodeLn("return \"\" + o;");
    }

    /**
     * Generates assignment of a result set column to a local variable, named as
     * the original field name. If the field type is not a basic type, the value
     * is determined by using the getObject-method in conjunction with appropriate
     * casting.
     *
     * @param entity
     * @param attribute
     * @return
     */
    private String generateLocalVarFromResultSet(Entity entity, Attribute attribute) {
        String voSimpleClassName = entity.getVoSimpleClassName();
        String fieldType = attribute.getJavaTypeSimpleName();
        String out = fieldType + " " + attribute.getFieldName() + " = ";
//        if (attribute.isPrimitiveType()) {
//            String resultSetGetter = fieldType.substring(0, 1).toUpperCase() + fieldType.substring(1);
        String resultSetGetter = TypeMapper.getResultSetGetMethod(fieldType);
            out += "resultSet." + resultSetGetter;
//        } else {
//            out += "(" + fieldType + ") resultSet.getObject";
//        }
        out += "(" + voSimpleClassName + "." + attribute.getConstName() + ");";

        return out;
    }

    private String createGetConnectionStatement() {
//        return "Connection connection = " + this.connectionManagerSimpleClassName + ".getInstance().openConnection(" + this.entity.getDaoSimpleClassName() + ".class);";
        return "Connection connection = " + this.connectionManagerSimpleClassName + ".openConnection(" + this.entity.getDaoSimpleClassName() + ".class);";
    }

    private String generateReleaseConnectionStatement() {
//        return this.connectionManagerSimpleClassName + ".getInstance().releaseConnection(connection, " + this.entity.getDaoSimpleClassName() + ".class);";
        return this.connectionManagerSimpleClassName + ".releaseConnection(connection, " + this.entity.getDaoSimpleClassName() + ".class);";
    }

    private GeneratorConfiguration getGeneratorConfiguration() {
        return GeneratorContext.getInstance().getGeneratorConfiguration();
    }

}

