package com.e2i.wemeet.exception.unauthorized;

import com.e2i.wemeet.exception.CustomException;
import com.e2i.wemeet.exception.ErrorCode;
import java.nio.file.AccessDeniedException;
import lombok.Getter;

/*
 * 리소스에 대한 접근 권한이 없을 때 발생
 */
public class UnAuthorizedException extends CustomException {

    public UnAuthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
