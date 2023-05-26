package de.arthurpicht.barnacle.helper;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionHelper {

    public static String getStackTrace(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

}
