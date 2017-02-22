package org.snorochevskiy.mapper.test;

import org.snorochevskiy.IMapper;
import org.snorochevskiy.dtos.HumanDto;
import org.snorochevskiy.entities.HumanEntity;

public class HumanMapper implements IMapper<HumanEntity, HumanDto> {

    @Override
    public HumanDto map(HumanEntity obj) {
        HumanDto dto = new HumanDto();

        dto.setName(obj.getName());
        dto.setAge(obj.getAge());
        dto.setWight(obj.getWight());

        return dto;
    }
}
