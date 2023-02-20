package de.arthurpicht.barnacle.model;

import de.arthurpicht.barnacle.configuration.generator.GeneratorConfiguration;
import de.arthurpicht.barnacle.configuration.generator.GeneratorConfigurationBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InvalidAITest {

    @Test
    public void test() throws ERMBuilderException {
        GeneratorConfigurationBuilder generatorConfigurationBuilder = new GeneratorConfigurationBuilder(
                "test.vof.myPackage.name",
                "test.vo.myPackage.name",
                "test.vob.myPackage.name",
                "test.dao.myPackage.name"
        );
        GeneratorConfiguration generatorConfiguration = generatorConfigurationBuilder.build();

        List<Class<?>> classList = new ArrayList<>();
        classList.add(InvalidAIVOF.class);

        ERMBuilderException e = Assertions.assertThrows(
                ERMBuilderException.class,
                () -> EntityRelationshipModelBuilder.execute(generatorConfiguration, classList));

        assertEquals(
                "More than one autoIncrement field found in VOF file [InvalidAIVOF]: [id1], [id2].",
                e.getMessage());
    }

}
