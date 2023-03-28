package de.arthurpicht.barnacle.model;

import de.arthurpicht.barnacle.annotations.Annotations.Barnacle;
import de.arthurpicht.barnacle.annotations.Annotations.ColumnName;
import de.arthurpicht.barnacle.annotations.Annotations.PrimaryKey;

@SuppressWarnings("unused")
@Barnacle
public class InvalidAIVOF {

    @Barnacle
    @PrimaryKey(autoIncrement = true)
    protected String id1;

    @Barnacle
    @PrimaryKey(autoIncrement = true)
    protected String id2;

    @Barnacle
    @ColumnName("first_name")
    protected String firstName;

    @Barnacle
    @ColumnName("last_name")
    protected String lastName;

}
