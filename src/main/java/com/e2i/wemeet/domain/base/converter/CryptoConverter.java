package com.e2i.wemeet.domain.base.converter;

import com.e2i.wemeet.util.encryption.TwoWayEncryption;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

@Converter
@Component
public class CryptoConverter implements AttributeConverter<String, String> {

    private final TwoWayEncryption twoWayEncryption;

    public CryptoConverter(TwoWayEncryption twoWayEncryption) {
        this.twoWayEncryption = twoWayEncryption;
    }

    // DB에 저장될 때 (암호화)
    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) {
            return null;
        }
        return twoWayEncryption.encrypt(attribute);
    }

    // DB에서 읽어올 때 (복호화)
    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return twoWayEncryption.decrypt(dbData);
    }
}
