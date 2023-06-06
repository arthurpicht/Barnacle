package de.arthurpicht.barnacle.helper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringHelperTest {

    @Test
    void shiftFirstLetterToUpperCase() {
        String string = StringHelper.shiftFirstLetterToUpperCase("test");
        assertEquals("Test", string);
    }

    @Test
    void shiftFirstLetterToUpperCaseEmpty() {
        String string = StringHelper.shiftFirstLetterToUpperCase("");
        assertEquals("", string);
    }

    @Test
    void shiftFirstLetterToUpperCaseAlreadyDone() {
        String string = StringHelper.shiftFirstLetterToUpperCase("Test");
        assertEquals("Test", string);
    }

}