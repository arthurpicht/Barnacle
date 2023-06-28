# Barnacle release history

* v0.1.0 22.08.2019 Last version built with ant
* v0.2.0 22.08.2019 Build migrated to gradle
* v0.2.1 23.01.2020 minor fixes
* v0.3.0 07.06.2023 complete rework
    * new configuration property `dialect`in `general` section
    * schema generation: H2 support
    * schema generation: all primary key fields are specified as NOT NULL explicitly
    * consistent handling of null values for fields of object types.
    * CLI
* v0.3.1 07.06.2023 minor fix
* v0.3.2 27.06.2023
    * `@CloneableVo` annotation: VO implements `Cloneable` interface and `clone` method
      only if annotation is given on type level.
    * `@SerializableVo` annotation: VO implements `Serializable` interface and defines `serialVersionUID`
  


