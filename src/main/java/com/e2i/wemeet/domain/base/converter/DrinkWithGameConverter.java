package com.e2i.wemeet.domain.base.converter;

import com.e2i.wemeet.domain.team.data.DrinkWithGame;
import jakarta.persistence.AttributeConverter;

public class DrinkWithGameConverter implements AttributeConverter<DrinkWithGame, Integer> {

    @Override
    public Integer convertToDatabaseColumn(DrinkWithGame attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getKey();
    }

    @Override
    public DrinkWithGame convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        return DrinkWithGame.findByKey(dbData);
    }
}
