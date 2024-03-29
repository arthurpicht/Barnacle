package de.arthurpicht.barnacle.codeGenerator.java;

import de.arthurpicht.barnacle.Const;
import de.arthurpicht.barnacle.Const.Encoding;
import de.arthurpicht.barnacle.codeGenerator.CodeGeneratorException;
import de.arthurpicht.barnacle.codeGenerator.java.vo.VoConstructorGenerator;
import de.arthurpicht.barnacle.configuration.generator.GeneratorConfiguration;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ClassGenerator {

    protected final GeneratorConfiguration generatorConfiguration;
    protected final String canonicalClassName;
    protected String baseClassSimpleName;
    protected final List<String> implementedInterfaces;
    protected final ImportGenerator importGenerator;
    protected final ConstantGenerator constantGenerator;
    protected ConstructorGenerator constructorGenerator = null;
    protected final List<MethodGenerator> methodGeneratorList;
    protected LoggerGenerator loggerGenerator;
    protected final LocalStringConstGenerator localStringConstGenerator;

    public ClassGenerator(String canonicalClassName, GeneratorConfiguration generatorConfiguration) {
        this.generatorConfiguration = generatorConfiguration;
        this.canonicalClassName = canonicalClassName;
        this.baseClassSimpleName = "";
        this.implementedInterfaces = new ArrayList<>();
        this.importGenerator = new ImportGenerator(this);
        this.constantGenerator = new ConstantGenerator();
        this.methodGeneratorList = new ArrayList<>();
        this.loggerGenerator = null;
        this.localStringConstGenerator = new LocalStringConstGenerator();
    }

    public String getPackageName() {
        return JavaGeneratorHelper.getPackageNameFromCanonicalClassName(this.canonicalClassName);
    }

    public String getSimpleClassName() {
        return JavaGeneratorHelper.getSimpleClassNameFromCanonicalClassName(this.canonicalClassName);
    }

    public void setBaseClass(Class<?> baseClass) {
        this.importGenerator.addImport(baseClass);
        this.baseClassSimpleName = baseClass.getSimpleName();
    }

    public ImportGenerator getImportGenerator() {
        return this.importGenerator;
    }

    public void addImplementedInterface(Class<?> implementedInterface) {
        this.importGenerator.addImport(implementedInterface);
        this.implementedInterfaces.add(implementedInterface.getSimpleName());
    }

    public ConstantGenerator getConstantGenerator() {
        return this.constantGenerator;
    }

    public LocalStringConstGenerator getLocalStringConstGenerator() {
        return this.localStringConstGenerator;
    }

    public VoConstructorGenerator getVOConstructorGenerator() throws CodeGeneratorException {
        if (this.constructorGenerator != null) {
            if (constructorGenerator instanceof VoConstructorGenerator) {
                return (VoConstructorGenerator) constructorGenerator;
            }
            throw new CodeGeneratorException("Constructor preexisting from different type.");
        }
        String simpleClassName = JavaGeneratorHelper.getSimpleClassNameFromCanonicalClassName(this.canonicalClassName);
        VoConstructorGenerator voConstructorGenerator = new VoConstructorGenerator(simpleClassName);
        this.constructorGenerator = voConstructorGenerator;

        return voConstructorGenerator;
    }

    public MethodGenerator getNewMethodGenerator() {
        MethodGenerator methodGenerator = new MethodGenerator(this);
        this.methodGeneratorList.add(methodGenerator);
        return methodGenerator;
    }

    public LoggerGenerator getLoggerGenerator() {
        if (this.loggerGenerator == null)
            this.loggerGenerator = LoggerGenerator.getInstance(this, LoggerGenerator.LoggerType.SLF4J, this.generatorConfiguration);
        return this.loggerGenerator;
    }

    public void generate() throws CodeGeneratorException {

        String sourceGenFolder = this.generatorConfiguration.getSrcGenDir();
        String fileName = sourceGenFolder + this.canonicalClassName.replace('.', '/') + ".java";

        generate(Paths.get(fileName));
    }

    public void generate(Path destination) throws CodeGeneratorException {
        try {
            PrintWriter printWriter;
            Encoding encoding = this.generatorConfiguration.getEncodingSource();
            if (encoding.equals(Const.Encoding.UTF)) {
                printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(destination.toFile()), StandardCharsets.UTF_8));
            } else if (encoding.equals(Const.Encoding.ISO)) {
                printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(destination.toFile()), StandardCharsets.ISO_8859_1));
            } else {
                printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(destination.toFile())));
            }

            SourceCache sourceCache = new SourceCache(printWriter);

            this.generatePackageDeclaration(sourceCache);

            this.importGenerator.generate(sourceCache);

            if (!this.generatorConfiguration.isOmitJavaDoc())
                this.generateJavaDocHeader(sourceCache);

            this.generateClassSignature(sourceCache);

            if (this.loggerGenerator != null) {
                this.loggerGenerator.generateInitialization(sourceCache);
            }

            this.constantGenerator.generate(sourceCache);

            this.localStringConstGenerator.generate(sourceCache);

            if (this.constructorGenerator != null) {
                this.constructorGenerator.generate(sourceCache);
            }

            for (MethodGenerator mGenerator : this.methodGeneratorList) {
                mGenerator.generate(sourceCache);
                sourceCache.addLine("");
            }

            sourceCache.addLine("}");

            sourceCache.flush();
            printWriter.close();
        } catch (FileNotFoundException e) {
            throw new CodeGeneratorException(e);
        }
    }


    private void generatePackageDeclaration(SourceCache sourceCache) {
        sourceCache.addLine("// DO NOT CHANGE THIS FILE MANUALLY!");
        sourceCache.addLine("package " + this.getPackageName() + ";");
        sourceCache.addLine("");
    }

    private void generateClassSignature(SourceCache sourceCache) {
        String classSignature = "public class " + JavaGeneratorHelper.getSimpleClassNameFromCanonicalClassName(this.canonicalClassName);

        if (!this.baseClassSimpleName.equals("")) {
            classSignature += " extends " + this.baseClassSimpleName;
        }

        if (this.implementedInterfaces.size() > 0) {
            classSignature += " implements ";
        }

        StringBuilder interfacesToken = new StringBuilder();
        for (String implementedInterface : this.implementedInterfaces) {
            if (!interfacesToken.toString().equals("")) {
                interfacesToken.append(", ");
            }
            interfacesToken.append(implementedInterface);
        }
        classSignature += interfacesToken;

        sourceCache.addLine(classSignature + " {");
        sourceCache.addLine("");
    }

    private void generateJavaDocHeader(SourceCache sourceCache) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String datum = simpleDateFormat.format(System.currentTimeMillis());

        sourceCache.addLine("/**");
        sourceCache.addLine(" * Generated by " + Const.VERSION_STRING + " on " + datum);
        sourceCache.addLine(" * https://github.com/arthurpicht/Barnacle");
        sourceCache.addLine(" * created 2007 - 2023 by Arthur Picht, Düren and Düsseldorf, Germany");
        sourceCache.addLine(" *");
        sourceCache.addLine(" * DO NOT CHANGE THIS FILE MANUALLY!");
        sourceCache.addLine(" */");
    }

}
