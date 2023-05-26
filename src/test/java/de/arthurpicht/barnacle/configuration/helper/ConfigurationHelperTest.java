package de.arthurpicht.barnacle.configuration.helper;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.Map.Entry;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationHelperTest {

    @Test
    void asPropertiesSimple() {
        List<String> stringList = List.of("para1=test", "para2=foo", "para3=foo bar");
        Map<String, String> properties = ConfigurationHelper.asProperties(stringList);

        assertEquals(3, properties.size());
        Set<Entry<String, String>> entrySet = properties.entrySet();
        List<Entry<String, String>> entryList = new ArrayList<>(entrySet);

        Entry<String, String> entry = entryList.get(0);
        assertEquals("para1", entry.getKey());
        assertEquals("test", entry.getValue());

        entry = entryList.get(1);
        assertEquals("para2", entry.getKey());
        assertEquals("foo", entry.getValue());

        entry = entryList.get(2);
        assertEquals("para3", entry.getKey());
        assertEquals("foo bar", entry.getValue());
    }

    @Test
    void asPropertiesWhitespace() {
        List<String> stringList = List.of("para1=test", " para2 = foo", "para3 = foo bar");
        Map<String, String> properties = ConfigurationHelper.asProperties(stringList);

        assertEquals(3, properties.size());
        Set<Entry<String, String>> entrySet = properties.entrySet();
        List<Entry<String, String>> entryList = new ArrayList<>(entrySet);

        Entry<String, String> entry = entryList.get(0);
        assertEquals("para1", entry.getKey());
        assertEquals("test", entry.getValue());

        entry = entryList.get(1);
        assertEquals("para2", entry.getKey());
        assertEquals("foo", entry.getValue());

        entry = entryList.get(2);
        assertEquals("para3", entry.getKey());
        assertEquals("foo bar", entry.getValue());
    }

}