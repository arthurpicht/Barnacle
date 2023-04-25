package de.arthurpicht.barnacle.configuration.helper;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationHelperTest {

    @Test
    void asProperties() {

        List<String> stringList = List.of("para1=test, para2=foo,para3=bar");

        Map<String, String> properties = ConfigurationHelper.asProperties(stringList);

        for (Map.Entry<String, String> entry : properties.entrySet()) {
            System.out.println("key=" + entry.getKey() + "; value=" + entry.getValue());
        }

        assertEquals(3, properties.size());


    }
}