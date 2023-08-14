package com.e2i.wemeet.domain.base.converter;

import com.e2i.wemeet.domain.team.data.Region;
import jakarta.persistence.AttributeConverter;

public class RegionConverter implements AttributeConverter<Region, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Region attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getKey();
    }

    @Override
    public Region convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        return Region.findByKey(dbData);
    }
}
