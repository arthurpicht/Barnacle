package de.arthurpicht.barnacle.generator.java;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.arthurpicht.barnacle.BarnacleInitializer;
import de.arthurpicht.barnacle.BarnacleInitializer.Encoding;
import de.arthurpicht.barnacle.configuration.GeneratorConfiguration;
import de.arthurpicht.barnacle.context.GeneratorContext;
import de.arthurpicht.barnacle.exceptions.GeneratorException;
import de.arthurpicht.barnacle.helper.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("rawtypes")
public class ClassGenerator {

    private static Logger logger = LoggerFactory.getLogger("BARNACLE");

    protected String canonicalClassName;
    protected String baseClassSimpleName;
    protected List<String> implementedInterfaces;
    protected ImportGenerator importGenerator;
    protected ConstantGenerator constantGenerator;
    protected ConstructorGenerator constructorGenerator = null;
    protected List<MethodGenerator> methodGeneratorList;
    protected LoggerGenerator loggerGenerator;

    /**
     * Initializes ClassGenerator object by passed canonical class
     * name.
     *
     * @param canonicalClassName
     */
    public ClassGenerator(String canonicalClassName) {

        this.canonicalClassName = canonicalClassName;
        this.baseClassSimpleName = new String();
        this.implementedInterfaces = new ArrayList<String>();
        this.importGenerator = new ImportGenerator(this);
        this.constantGenerator = new ConstantGenerator(this);
        this.methodGeneratorList = new ArrayList<MethodGenerator>();
        this.loggerGenerator = null;
    }

    /**
     * Returns the package name the class to be generated
     * belongs to.
     *
     * @return
     */
    public String getPackageName() {
        return Helper.getPackageNameFromCanonicalClassName(this.canonicalClassName);
    }

    public String getCanonicalClassName() {
        return this.canonicalClassName;
    }

    public String getSimpleClassName() {
        return Helper.getSimpleClassNameFromCanonicalClassName(this.canonicalClassName);
    }

    /**
     * Defines class from which the class to be generated is
     * extended from.
     *
     * @param baseClass
     */
    public void setBaseClass(Class baseClass) {
        this.importGenerator.addImport(baseClass);
        this.baseClassSimpleName = baseClass.getSimpleName();
    }

    /**
     * Returns import generator associated with this class
     * generator.
     *
     * @return
     */
    public ImportGenerator getImportGenerator() {
        return this.importGenerator;
    }

    /**
     * Adds interface to list of implemented interfaces. Adds interface
     * also to list of imported classes.
     *
     * @param implementedInterface
     */
    public void addImplementedInterface(Class implementedInterface) {
        this.importGenerator.addImport(implementedInterface);
        this.implementedInterfaces.add(implementedInterface.getSimpleName());
    }

    /**
     * Returns constant generator associated with this class
     * generator.
     *
     * @return
     */
    public ConstantGenerator getConstantGenerator() {
        return this.constantGenerator;
    }

    /**
     * Returns the VOConstructorGenerator associated with this class generator.
     * If no one is preexisting, a new instance is constructed.
     * If a constructor generator of a diffenent type is preexisting, an
     * GeneratorException is thrown.
     *
     * @return
     * @throws GeneratorException
     */
    public VoConstructorGenerator getVOConstructorGenerator() throws GeneratorException {

        if (this.constructorGenerator != null) {
            if (constructorGenerator instanceof VoConstructorGenerator) {
                return (VoConstructorGenerator) constructorGenerator;
            }
            throw new GeneratorException("Constructor preexisting from different type.");
        }
        String simpleClassName = Helper.getSimpleClassNameFromCanonicalClassName(this.canonicalClassName);
        VoConstructorGenerator voConstructorGenerator = new VoConstructorGenerator(simpleClassName);
        this.constructorGenerator = voConstructorGenerator;

        return voConstructorGenerator;
    }

    /**
     * MethodGenerator object factory. Creates method generator,
     * adds it to internal list of methods and gives it back for
     * later configuration.
     *
     * @return
     */
    public MethodGenerator getNewMethodGenerator() {
        MethodGenerator methodGenerator = new MethodGenerator(this);
        this.methodGeneratorList.add(methodGenerator);
        return methodGenerator;
    }

    /**
     * Returns logger generator associated with this class
     * generator. Calling this method indicates, that a logger
     * is used for generated class. Never calling this method
     * means, that no logger is used.
     *
     * @return
     */
    public LoggerGenerator getLoggerGenerator() {
        if (this.loggerGenerator == null) {
            this.loggerGenerator = LoggerGenerator.getInstance(this, BarnacleInitializer.getLoggerType());
        }
        return this.loggerGenerator;
    }

    /**
     * Generates class to passed sourced folder.
     *
     * @throws GeneratorException
     */
    public void generate() throws GeneratorException {

        GeneratorConfiguration generatorConfiguration = GeneratorContext.getInstance().getGeneratorConfiguration();

        String sourceGenFolder = generatorConfiguration.getSrcGenDir();
        String fileName = sourceGenFolder + this.canonicalClassName.replace('.', '/') + ".java";
        logger.debug("Generating " + fileName);

        try {
            PrintWriter printWriter = null;
            Encoding encoding = generatorConfiguration.getEncodingSource();
            if (encoding.equals(Encoding.UTF)) {
                printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"));
            } else if (encoding.equals(Encoding.ISO)) {
                printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fileName), "ISO-8859-1"));
            } else {
                printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fileName)));
            }

            SourceCache sourceCache = new SourceCache(printWriter);

            this.generatePackageDeclaration(sourceCache);

            this.importGenerator.generate(sourceCache);

            this.generateJavaDocHeader(sourceCache);

            this.generateClassSignature(sourceCache);

            if (this.loggerGenerator != null) {
                this.loggerGenerator.generateInitialization(sourceCache);
            }

            this.constantGenerator.generate(sourceCache);

            if (this.constructorGenerator != null) {
                this.constructorGenerator.generate(sourceCache);
            }

            //
            // methods
            //
            for (MethodGenerator mGenerator : this.methodGeneratorList) {
                mGenerator.generate(sourceCache);
                sourceCache.addLine("");
            }

            sourceCache.addLine("}");

            sourceCache.flush();
//			printWriter.flush();
            printWriter.close();
        } catch (FileNotFoundException e) {
            throw new GeneratorException(e);
        } catch (UnsupportedEncodingException e) {
            throw new GeneratorException(e);
        }


        // TODO init pw
        // init SouceCache
        // write to sc
        // flush sc
    }

    private void generatePackageDeclaration(SourceCache sourceCache) {
        sourceCache.addLine("// DO NOT CHANGE THIS FILE MANUALLY!");
        sourceCache.addLine("package " + this.getPackageName() + ";");
        sourceCache.addLine("");
    }

    private void generateClassSignature(SourceCache sourceCache) {

        String classSignature = "public class " + Helper.getSimpleClassNameFromCanonicalClassName(this.canonicalClassName);

        if (!this.baseClassSimpleName.equals("")) {
            classSignature += " extends " + this.baseClassSimpleName;
        }

        if (this.implementedInterfaces.size() > 0) {
            classSignature += " implements ";
        }
        boolean sequence = false;
        for (String implementedInterface : this.implementedInterfaces) {
            if (sequence) {
                classSignature += ", ";
            }
            classSignature += implementedInterface;
        }

        sourceCache.addLine(classSignature + " {");
        sourceCache.addLine("");
    }

    private void generateJavaDocHeader(SourceCache sourceCache) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String datum = simpleDateFormat.format(System.currentTimeMillis());

        sourceCache.addLine("/**");
        sourceCache.addLine(" * Generated by " + BarnacleInitializer.VERSION + " on " + datum);
        sourceCache.addLine(" * https://github.com/arthurpicht/Barnacle");
        sourceCache.addLine(" * created 2007 - 2018 by Arthur Picht, Düren and Düsseldorf, Germany");
        sourceCache.addLine(" *");
        sourceCache.addLine(" * DO NOT CHANGE THIS FILE MANUALLY!");
        sourceCache.addLine(" */");
    }

    /**
     * Generates a variable name from a simple class name by changing the
     * first letter to lower case.
     *
     * @param simpleClassName
     * @return
     */
    protected String generateVarNameFromSimpleClassName(String simpleClassName) {
        return simpleClassName.substring(0, 1).toLowerCase() + simpleClassName.substring(1, simpleClassName.length());
    }


}
