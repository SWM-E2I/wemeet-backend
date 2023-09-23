package com.e2i.wemeet.domain.base.converter;

import com.e2i.wemeet.domain.member.data.Gender;
import com.e2i.wemeet.exception.badrequest.InvalidDatabaseKeyToEnumException;
import jakarta.persistence.AttributeConverter;

public class GenderConverter implements AttributeConverter<Gender, String> {

    @Override
    public String convertToDatabaseColumn(Gender attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getKey();
    }

    @Override
    public Gender convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        } else if (dbData.equals(Gender.MAN.getKey())) {
            return Gender.MAN;
        } else if (dbData.equals(Gender.WOMAN.getKey())) {
            return Gender.WOMAN;
        }
        throw new InvalidDatabaseKeyToEnumException();
    }
}
