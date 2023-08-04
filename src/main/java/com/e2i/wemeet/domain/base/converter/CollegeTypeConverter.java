package com.e2i.wemeet.domain.base.converter;

import com.e2i.wemeet.domain.member.data.CollegeType;
import jakarta.persistence.AttributeConverter;

public class CollegeTypeConverter implements AttributeConverter<CollegeType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(CollegeType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getKey();
    }

    @Override
    public CollegeType convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        return CollegeType.findByKey(dbData);
    }
}
