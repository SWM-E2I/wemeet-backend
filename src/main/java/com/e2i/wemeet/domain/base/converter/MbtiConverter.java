package com.e2i.wemeet.domain.base.converter;

import com.e2i.wemeet.domain.member.data.Mbti;
import jakarta.persistence.AttributeConverter;

public class MbtiConverter implements AttributeConverter<Mbti, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Mbti attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getKey();
    }

    @Override
    public Mbti convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        return Mbti.findByKey(dbData);
    }
}
