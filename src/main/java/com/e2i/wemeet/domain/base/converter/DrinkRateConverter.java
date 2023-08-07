package com.e2i.wemeet.domain.base.converter;

import com.e2i.wemeet.domain.team.data.DrinkRate;
import jakarta.persistence.AttributeConverter;

public class DrinkRateConverter implements AttributeConverter<DrinkRate, Integer> {

    @Override
    public Integer convertToDatabaseColumn(DrinkRate attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getKey();
    }

    @Override
    public DrinkRate convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        return DrinkRate.findByKey(dbData);
    }
}
