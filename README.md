# Barnacle

Barnacle is an ultra-lightweight object-relational mapping tool (ORM-tool) for Java.
Based on annotated classes, it features generation of value-objects (VOs), data-access-objects (DAOs)
and SQL-scripts for schema creation. Generated SQL is designed to be compatible at least with
MySQL. 

Barnacle uses no reflection on runtime. Generated classes can be applied without any
runtime dependencies back to Barnacle. Optionally, barnacle can be referenced on runtime to
take advantage of a built-in JDBC connection provider.

## Status

Under development. Applicable for restricted use cases. 
Simply check  whether generated classes suit your requirements. Good luck!

## Download from Maven-Repo

For gradle or maven builds, Barnacle can be obtained from [jitpack](https://jitpack.io).
In gradle builds, add *jitpack* to the list of your repositories and add the following
dependency declaration:

    repositories {
        jcenter()
        maven { url "https://jitpack.io" }
    }
   
    dependencies {
        implementation 'com.github.arthurpicht:Barnacle:master-SNAPSHOT'    
    }
    
Use `master-SNAPSHOT` for the latest development version or `v0.2.0` for the latest release.

Don't forget to add a JDBC driver to your dependencies, like

    implementation 'org.mariadb.jdbc:mariadb-java-client:2.4.3'

* [AP-Configuration](https://github.com/arthurpicht/AP-Configuration)
* [slf4j-api-1.7.25.jar](http://search.maven.org/#artifactdetails%7Corg.slf4j%7Cslf4j-api%7C1.7.25%7Cjar)
* JDBC-Driver, e.g. [mysql-connector-java-5.1.45-bin.jar](http://search.maven.org/#artifactdetails%7Cmysql%7Cmysql-connector-java%7C5.1.45%7Cjar)
* Logging-Tool of your choice, compatible with slf4j, e.g. Logback: [logback-core-1.2.3.jar](http://search.maven.org/#artifactdetails%7Cch.qos.logback%7Clogback-core%7C1.2.3%7Cjar)
, [logback-classic-1.2.3](http://search.maven.org/#artifactdetails%7Cch.qos.logback%7Clogback-classic%7C1.2.3%7Cjar)

## Build

1. Clone repository.

        $ git clone git@github.com:arthurpicht/Barnacle.git
        
    or
    
        $ git clone https://github.com/arthurpicht/Barnacle.git

2. Build Barnacle with [gradle](https://gradle.org):

        $ gradle jar

    Resulting JAR can be found in *Barnacle/build/libs*.

## Prepare Barnacle as a tool

In order to prepare Barnacle to be used as a command line tool, build Barnacle as 
described and proceed the following steps.

1. By calling

        $ gradle prepareTooling
        
    all dependent JARs will be copied to *Barnacle/bin/libs*
    
2. Put `Barnacle\bin` on your *PATH*.

3. Configure *barnacle.conf*, including the *\[generator\]* section.

4. Call

       $ barnacle path/to/your/barnacle.conf

## Include Barnacle into your gradle build

Based on the previous steps, Barnacle can be included into gradle build 
of your own project as simple as that:

    task barnacle(type: Exec) {
        mkdir "$buildDir/sql"
        commandLine "barnacle", "path/to/your/custom/barnacle.conf"
    }
 
## Generator configuration

| parameter                        | description                                                                                                                                            | default      |
|----------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------|--------------|
| dialect                          | SQL dialect. One of `MYSQL` or `H2`                                                                                                                    | MYSQL        |
| src_dir                          | source dirctory for VOF files                                                                                                                          | src/         |
| src_gen_dir                      | source direcory for generated files                                                                                                                    | src-gen/     |
| vof_package_name                 | package name for VOF files                                                                                                                             | `mandatory`  |
| vo_package_name                  | package name for VO files                                                                                                                              | `mandatory`  |
| vob_package_name                 | package name for VOB files                                                                                                                             | `mandatory`  |
| dao_package_name                 | package name for DAO files                                                                                                                             | `mandatory`  |
| execute_on_db                    | create resulting schema on DB                                                                                                                          | false        |
| create_script                    | create SQL script                                                                                                                                      | false        |
| create_script                    | create SQL script                                                                                                                                      | false        |
| script_file                      | name of SQL script file                                                                                                                                | barnacle.sql |
| encoding_db                      | encoding for database contents. One of `ISO`, `UTF` or `DEFAULT`.                                                                                      | DEFAULT      |
| encoding_source                  | encoding for generated java sources. One of `ISO`, `UTF` or `DEFAULT`.                                                                                 | ISO          |
| encoding_source                  | encoding for generated java sources. One of `ISO`, `UTF` or `DEFAULT`.                                                                                 | ISO          |
| connection_manager_class         | canonical class name of custom connection manager class. If not set internal class is used.                                                            | `internal`   |
| connection_exception_class       | canonical class name of custom connection exception class. If not set internal class is used.                                                          | `internal`   |
| entity_not_found_exception_class | canonical class name of custom entity not found exception class. If not set internal class is used.                                                    | `internal`   |
| doa_logger_name                  | name of logger used in generated DAO classes.                                                                                                          |              |
| omit_javadoc                    | Omit generation of any javadoc comment in generated classes. This can be useful if generated code is subject to tests as javadoc contains a timestamp. | false        |



### Programmatic configuration




