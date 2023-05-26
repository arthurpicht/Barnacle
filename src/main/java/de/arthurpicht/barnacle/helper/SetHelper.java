package de.arthurpicht.barnacle.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SetHelper {

    public static <T> List<T> toList(Set<T> set) {
        return new ArrayList<>(set);
    }

}
