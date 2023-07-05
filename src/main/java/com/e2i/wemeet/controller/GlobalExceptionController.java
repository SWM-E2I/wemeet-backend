package com.e2i.wemeet.controller;

import static com.e2i.wemeet.exception.ErrorCode.METHOD_ARGUMENT_NOT_VALID;
import static com.e2i.wemeet.exception.ErrorCode.MISSING_REQUEST_PARAMETER;
import static com.e2i.wemeet.exception.ErrorCode.UNAUTHORIZED_ROLE;
import static com.e2i.wemeet.exception.ErrorCode.UNEXPECTED_INTERNAL;

import com.e2i.wemeet.dto.response.ResponseStatus;
import com.e2i.wemeet.exception.ErrorCode;
import com.e2i.wemeet.exception.ErrorResponse;
import com.e2i.wemeet.exception.badrequest.DuplicatedValueException;
import com.e2i.wemeet.exception.badrequest.InvalidValueException;
import com.e2i.wemeet.exception.internal.InternalServerException;
import com.e2i.wemeet.exception.notfound.NotFoundException;
import com.e2i.wemeet.exception.unauthorized.UnAuthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionController {

    private static final String ERROR_LOG_FORMAT = "Error Class : {}, Error Code : {}, Message : {}";

    private static final String UNAUTHORIZED_LOG_FORMAT = "UnAuthorized Error Class : {}, Error Code : {}, Message : {}";
    private final MessageSourceAccessor messageSourceAccessor;

    // @PreAuthorize 예외 핸들링
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationException(
        final AccessDeniedException e) {
        final ErrorCode errorCode = UNAUTHORIZED_ROLE;
        final int code = errorCode.getCode();
        final String message = messageSourceAccessor.getMessage(errorCode.getMessageKey());

        log.info(UNAUTHORIZED_LOG_FORMAT, e.getClass().getSimpleName(), code, message);
        return new ResponseEntity<>(
            new ErrorResponse(ResponseStatus.FAIL, code, message), HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(InvalidValueException.class)
    public ResponseEntity<ErrorResponse> handleInvalidValueException(
        final InvalidValueException e) {
        final int code = e.getErrorCode().getCode();
        final String message = messageSourceAccessor.getMessage(e.getMessage());

        log.info(ERROR_LOG_FORMAT, e.getClass().getSimpleName(), code, message);
        return ResponseEntity.ok()
            .body(new ErrorResponse(ResponseStatus.FAIL, code, message));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(final NotFoundException e) {
        final int code = e.getErrorCode().getCode();
        final String message = messageSourceAccessor.getMessage(e.getMessage());

        log.info(ERROR_LOG_FORMAT, e.getClass().getSimpleName(), code, message);
        return ResponseEntity.ok()
            .body(new ErrorResponse(ResponseStatus.FAIL, code, message));
    }

    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnAuthorizedException(
        final UnAuthorizedException e) {
        final int code = e.getErrorCode().getCode();
        final String message = messageSourceAccessor.getMessage(e.getMessage());

        log.info(ERROR_LOG_FORMAT, e.getClass().getSimpleName(), code, message);
        return ResponseEntity.ok()
            .body(new ErrorResponse(ResponseStatus.FAIL, code, message));
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ErrorResponse> handleInternalServerException(
        final InternalServerException e) {
        final int code = e.getErrorCode().getCode();
        final String message = messageSourceAccessor.getMessage(e.getMessage());

        log.warn(ERROR_LOG_FORMAT, e.getClass().getSimpleName(), code, message);
        return ResponseEntity.internalServerError()
            .body(new ErrorResponse(ResponseStatus.ERROR, code, message));
    }

    @ExceptionHandler(DuplicatedValueException.class)
    public ResponseEntity<ErrorResponse> handleDuplicatedValueException(
        final DuplicatedValueException e) {
        final int code = e.getErrorCode().getCode();
        final String message = messageSourceAccessor.getMessage(e.getMessage());

        log.info(ERROR_LOG_FORMAT, e.getClass().getSimpleName(), code, message);
        return ResponseEntity.ok()
            .body(new ErrorResponse(ResponseStatus.FAIL, code, message));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException e) {
        String message = "";
        int code = METHOD_ARGUMENT_NOT_VALID.getCode();

        FieldError fieldError = e.getBindingResult().getFieldError();
        if (fieldError != null) {
            message = fieldError.getDefaultMessage();
        }

        log.info(ERROR_LOG_FORMAT, e.getClass().getSimpleName(), code, message);
        return ResponseEntity.ok()
            .body(new ErrorResponse(ResponseStatus.FAIL, code, message));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(final MissingServletRequestParameterException e) {
        final int code = MISSING_REQUEST_PARAMETER.getCode();
        final String message = messageSourceAccessor.getMessage(
            MISSING_REQUEST_PARAMETER.getMessageKey());

        log.error(ERROR_LOG_FORMAT, e.getClass().getName(), code, message);
        return ResponseEntity.internalServerError()
            .body(new ErrorResponse(code, message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(final Exception e) {
        final int code = UNEXPECTED_INTERNAL.getCode();
        final String message = messageSourceAccessor.getMessage(
            UNEXPECTED_INTERNAL.getMessageKey());

        log.error(ERROR_LOG_FORMAT, e.getClass().getName(), code, message);
        return ResponseEntity.internalServerError()
            .body(new ErrorResponse(ResponseStatus.ERROR, code, message));
    }
}
