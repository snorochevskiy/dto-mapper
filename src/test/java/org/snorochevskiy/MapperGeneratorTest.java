package org.snorochevskiy;

import org.junit.Assert;
import org.junit.Test;
import org.snorochevskiy.dtos.HumanDto;
import org.snorochevskiy.entities.HumanEntity;

public class MapperGeneratorTest {

    @Test
    public void testMapperGenerating() throws InstantiationException, IllegalAccessException {

        MapperGenerator generator = new MapperGenerator();

        IMapper<HumanEntity, HumanDto> mapper = generator.generate(HumanEntity.class, HumanDto.class);

        HumanEntity entity = new HumanEntity();
        entity.setName("Name");
        entity.setAge(50);
        entity.setWight(70);

        HumanDto dto = mapper.map(entity);
        Assert.assertNotNull(dto);
    }
}
