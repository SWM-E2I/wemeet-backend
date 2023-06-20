package com.e2i.wemeet.controller;

import com.e2i.wemeet.exception.ErrorResponse;
import com.e2i.wemeet.exception.badrequest.InvalidValueException;
import com.e2i.wemeet.exception.internal.InternalServerException;
import com.e2i.wemeet.exception.notfound.NotFoundException;
import com.e2i.wemeet.exception.unauthorized.UnAuthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionController {

    private static final String ERROR_LOG_FORMAT = "Error Class : {}, Error Code : {}, Message : {}";
    private final MessageSourceAccessor messageSourceAccessor;

    public GlobalExceptionController(MessageSource messageSource) {
        this.messageSourceAccessor = new MessageSourceAccessor(messageSource);
    }

    @ExceptionHandler(InvalidValueException.class)
    public ResponseEntity<ErrorResponse> handleInvalidValueException(final InvalidValueException e) {
        final int code = e.getErrorCode().getCode();
        final String message = messageSourceAccessor.getMessage(e.getMessage());

        log.info(ERROR_LOG_FORMAT, e.getClass().getSimpleName(), code, message);
        return ResponseEntity.ok()
                .body(new ErrorResponse(code, message));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
        final int code = e.getErrorCode().getCode();
        final String message = messageSourceAccessor.getMessage(e.getMessage());

        log.info(ERROR_LOG_FORMAT, e.getClass().getSimpleName(), code, message);
        return ResponseEntity.ok()
                .body(new ErrorResponse(code, message));
    }

    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnAuthorizedException(UnAuthorizedException e) {
        final int code = e.getErrorCode().getCode();
        final String message = messageSourceAccessor.getMessage(e.getMessage());

        log.info(ERROR_LOG_FORMAT, e.getClass().getSimpleName(), code, message);
        return ResponseEntity.ok()
                .body(new ErrorResponse(code, message));
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ErrorResponse> handleInternalServerException(InternalServerException e) {
        final int code = e.getErrorCode().getCode();
        final String message = messageSourceAccessor.getMessage(e.getMessage());

        log.warn(ERROR_LOG_FORMAT, e.getClass().getSimpleName(), code, message);
        return ResponseEntity.internalServerError()
                .body(new ErrorResponse(code, message));
    }
}
