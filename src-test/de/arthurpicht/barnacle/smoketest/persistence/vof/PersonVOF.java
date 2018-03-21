package de.arthurpicht.barnacle.smoketest.persistence.vof;

import de.arthurpicht.barnacle.annotations.Annotations.Barnacle;
import de.arthurpicht.barnacle.annotations.Annotations.ColumnName;
import de.arthurpicht.barnacle.annotations.Annotations.PrimaryKey;
import de.arthurpicht.barnacle.annotations.Annotations.TableName;

@Barnacle
@TableName("person")
public class PersonVOF {

    @Barnacle
    @ColumnName("first_name")
    @PrimaryKey
    protected String firstName;

    @Barnacle
    @ColumnName("name")
    protected String name;

    @Barnacle
    protected String street;

    @Barnacle
    protected int age;

}
