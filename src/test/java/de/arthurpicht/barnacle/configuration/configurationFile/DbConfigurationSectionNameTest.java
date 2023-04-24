package de.arthurpicht.barnacle.configuration.configurationFile;

import de.arthurpicht.barnacle.configuration.db.DbConfigurationType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DbConfigurationSectionNameTest {

    @Test
    void single() {
        DbConfigurationSectionName dbConfigurationSectionName = new DbConfigurationSectionName("db:single:test");
        assertEquals(
                DbConfigurationType.SINGLE,
                dbConfigurationSectionName.getDbConfigurationType());
        assertEquals("test", dbConfigurationSectionName.getName());
    }

    @Test
    void jndi() {
        DbConfigurationSectionName dbConfigurationSectionName = new DbConfigurationSectionName("db:jndi:test");
        assertEquals(
                DbConfigurationType.JNDI,
                dbConfigurationSectionName.getDbConfigurationType());
        assertEquals("test", dbConfigurationSectionName.getName());
    }

    @Test
    void neg1() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new DbConfigurationSectionName("db:something:test"));
    }

    @Test
    void neg2() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new DbConfigurationSectionName("db:something:"));
    }

    @Test
    void isDbConfigurationSectionName() {
        assertTrue(DbConfigurationSectionName.isDbConfigurationSectionName("db:single:test"));
    }

    @Test
    void isDbConfigurationSectionNameNeg() {
        assertFalse(DbConfigurationSectionName.isDbConfigurationSectionName("db:something:test"));
    }

}