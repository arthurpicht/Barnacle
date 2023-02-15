package de.arthurpicht.barnacle.generator.dao;

import de.arthurpicht.barnacle.configuration.GeneratorConfiguration;
import de.arthurpicht.barnacle.configuration.GeneratorConfigurationBuilder;
import de.arthurpicht.barnacle.exceptions.GeneratorException;
import de.arthurpicht.barnacle.generator.java.DaoGenerator;
import de.arthurpicht.barnacle.mapping.Attribute;
import de.arthurpicht.barnacle.mapping.Entity;
import de.arthurpicht.barnacle.processor.VOFProcessorEntityStage;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DaoTest {

    @Test
    public void test() throws GeneratorException {
        GeneratorConfigurationBuilder generatorConfigurationBuilder = new GeneratorConfigurationBuilder(
                "test.vof.package.name",
                "test.vo.package.name",
                "test.vob.package.name",
                "test.dao.package.name"
        );
        GeneratorConfiguration generatorConfiguration = generatorConfigurationBuilder.build();

        Class<?> vofClass = TestVOF.class;
        Entity entity = VOFProcessorEntityStage.process(vofClass, generatorConfiguration);
        assertNotNull(entity);

        List<Attribute> attributeList = entity.getAttributes();
        assertEquals(3, attributeList.size());

        Attribute attribute = attributeList.get(0);
        assertEquals("String", attribute.getJavaTypeSimpleName());

        attribute = attributeList.get(1);
        assertEquals("String", attribute.getJavaTypeSimpleName());

        attribute = attributeList.get(2);
        assertEquals("String", attribute.getJavaTypeSimpleName());

        DaoGenerator daoGenerator = new DaoGenerator(entity);
        daoGenerator.generate(Paths.get("src/test/temp/TestDAO.java"), generatorConfiguration);
    }

}
