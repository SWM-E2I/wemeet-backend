package com.e2i.wemeet.exception.token;

import com.e2i.wemeet.exception.ErrorCode;

// JWT 를 파싱할 수 없을 때 발생
public class JwtDecodeException extends TokenException {

    public JwtDecodeException() {
        super(ErrorCode.JWT_DECODE);
    }

    public JwtDecodeException(ErrorCode errorCode) {
        super(errorCode);
    }
}
