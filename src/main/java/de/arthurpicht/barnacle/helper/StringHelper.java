package de.arthurpicht.barnacle.helper;

public class StringHelper {

    public static String shiftFirstLetterToUpperCase(String string) {
        if (string.equals("")) return "";
        String shiftedString = string.substring(0, 1).toUpperCase();
        if (string.length() == 1) return shiftedString;
        shiftedString += string.substring(1);
        return shiftedString;
    }

}
