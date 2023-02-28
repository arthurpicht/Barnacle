package de.arthurpicht.barnacle.model;

import de.arthurpicht.barnacle.annotations.Annotations.Barnacle;
import de.arthurpicht.barnacle.annotations.Annotations.ColumnName;
import de.arthurpicht.barnacle.annotations.Annotations.PrimaryKey;

@Barnacle
public class NoPkFieldVOF {

    @Barnacle
    protected String myString1;

    @Barnacle
    protected String myString2;

}
