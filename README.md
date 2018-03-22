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

## Dependencies

* [AP-Configuration](https://github.com/arthurpicht/AP-Configuration)
* [slf4j-api-1.7.25.jar](http://search.maven.org/#artifactdetails%7Corg.slf4j%7Cslf4j-api%7C1.7.25%7Cjar)
* JDBC-Driver, e.g. [mysql-connector-java-5.1.45-bin.jar](http://search.maven.org/#artifactdetails%7Cmysql%7Cmysql-connector-java%7C5.1.45%7Cjar)
* Logging-Tool of your choice, compatible with slf4j, e.g. Logback: [logback-core-1.2.3.jar](http://search.maven.org/#artifactdetails%7Cch.qos.logback%7Clogback-core%7C1.2.3%7Cjar)
, [logback-classic-1.2.3](http://search.maven.org/#artifactdetails%7Cch.qos.logback%7Clogback-classic%7C1.2.3%7Cjar)


## Build

1. Clone repository.

2. Resolve third party dependencies:
    
        $ ant resolve.3p   

3. Build [AP-Configuration](https://github.com/arthurpicht/AP-Configuration) and copy resulting *JAR* to *Barnacle/lib* directory.

4. Build Barnacle:

        $ ant

    Resulting JAR can be found in *Barnacle/build/jar*.
