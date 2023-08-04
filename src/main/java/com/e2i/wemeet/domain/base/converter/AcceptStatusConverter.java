package com.e2i.wemeet.domain.base.converter;

import com.e2i.wemeet.domain.meeting.data.AcceptStatus;
import jakarta.persistence.AttributeConverter;

public class AcceptStatusConverter implements AttributeConverter<AcceptStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(AcceptStatus attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getKey();
    }

    @Override
    public AcceptStatus convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        return AcceptStatus.findByKey(dbData);
    }
}
