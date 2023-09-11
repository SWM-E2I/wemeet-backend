package com.e2i.wemeet.service.sns;

public record SnsEvent(
    String receivePhoneNumber,
    String message
) {

    public static SnsEvent of(final String receivePhoneNumber, final String message) {
        return new SnsEvent(receivePhoneNumber, message);
    }

}
