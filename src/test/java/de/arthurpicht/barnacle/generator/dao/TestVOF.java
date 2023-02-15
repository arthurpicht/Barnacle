package de.arthurpicht.barnacle.generator.dao;

import de.arthurpicht.barnacle.annotations.Annotations.Barnacle;
import de.arthurpicht.barnacle.annotations.Annotations.ColumnName;
import de.arthurpicht.barnacle.annotations.Annotations.PrimaryKey;

@Barnacle
public class TestVOF {

    @Barnacle
    @PrimaryKey
    protected String id;

    @Barnacle
    @ColumnName("first_name")
    protected String firstName;

    @Barnacle
    @ColumnName("last_name")
    protected String lastName;

}
