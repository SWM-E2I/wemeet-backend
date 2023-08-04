package com.e2i.wemeet.domain.base.converter;

import com.e2i.wemeet.domain.team.data.AdditionalActivity;
import jakarta.persistence.AttributeConverter;

public class AdditionalActivityConverter implements AttributeConverter<AdditionalActivity, Integer> {

    @Override
    public Integer convertToDatabaseColumn(AdditionalActivity attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getKey();
    }

    @Override
    public AdditionalActivity convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        return AdditionalActivity.findByKey(dbData);
    }
}
