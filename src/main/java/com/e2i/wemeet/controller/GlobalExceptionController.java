package com.e2i.wemeet.controller;

import static com.e2i.wemeet.exception.ErrorCode.DATA_ACCESS;
import static com.e2i.wemeet.exception.ErrorCode.HTTP_MESSAGE_NOT_READABLE;
import static com.e2i.wemeet.exception.ErrorCode.METHOD_ARGUMENT_NOT_VALID;
import static com.e2i.wemeet.exception.ErrorCode.MISSING_REQUEST_PARAMETER;
import static com.e2i.wemeet.exception.ErrorCode.NOT_EQUAL_ROLE_TO_TOKEN;
import static com.e2i.wemeet.exception.ErrorCode.UNAUTHORIZED_ROLE;
import static com.e2i.wemeet.exception.ErrorCode.UNEXPECTED_INTERNAL;
import static com.e2i.wemeet.exception.ErrorCode.VALIDATION_ERROR;

import com.e2i.wemeet.exception.ErrorCode;
import com.e2i.wemeet.exception.ErrorResponse;
import com.e2i.wemeet.exception.badrequest.BadRequestException;
import com.e2i.wemeet.exception.badrequest.DuplicatedValueException;
import com.e2i.wemeet.exception.badrequest.InvalidValueException;
import com.e2i.wemeet.exception.internal.InternalServerException;
import com.e2i.wemeet.exception.notfound.NotFoundException;
import com.e2i.wemeet.exception.token.NotEqualRoleToTokenException;
import com.e2i.wemeet.exception.unauthorized.UnAuthorizedException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.JDBCException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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
    private static final String SQL_ERROR_LOG_FORMAT = "SQL ERROR:: class : {}, \n *errorSql : {}, \n *message : {}";

    private final MessageSourceAccessor messageSourceAccessor;

    // @PreAuthorize 예외 핸들링
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationException(
        final AccessDeniedException e) {
        final ErrorCode errorCode = UNAUTHORIZED_ROLE;
        final int code = errorCode.getCode();
        final String message = messageSourceAccessor.getMessage(errorCode.getMessageKey());

        log.info(UNAUTHORIZED_LOG_FORMAT, e.getClass().getSimpleName(), code, message);
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ErrorResponse.fail(code, message));
    }

    @ExceptionHandler(InvalidValueException.class)
    public ResponseEntity<ErrorResponse> handleInvalidValueException(
        final InvalidValueException e) {
        final int code = e.getErrorCode().getCode();
        final String message = messageSourceAccessor.getMessage(e.getMessage());

        log.info(ERROR_LOG_FORMAT, e.getClass().getSimpleName(), code, message);
        return ResponseEntity
            .ok()
            .body(ErrorResponse.fail(code, message));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(final NotFoundException e) {
        final int code = e.getErrorCode().getCode();
        final String message = messageSourceAccessor.getMessage(e.getMessage());

        log.info(ERROR_LOG_FORMAT, e.getClass().getSimpleName(), code, message);
        return ResponseEntity
            .ok()
            .body(ErrorResponse.fail(code, message));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(final BadRequestException e) {
        final int code = e.getErrorCode().getCode();
        final String message = messageSourceAccessor.getMessage(e.getMessage());

        log.info(ERROR_LOG_FORMAT, e.getClass().getSimpleName(), code, message);
        return ResponseEntity
            .ok()
            .body(ErrorResponse.fail(code, message));
    }

    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnAuthorizedException(
        final UnAuthorizedException e) {
        final int code = e.getErrorCode().getCode();
        final String message = messageSourceAccessor.getMessage(e.getMessage());

        log.info(ERROR_LOG_FORMAT, e.getClass().getSimpleName(), code, message);
        return ResponseEntity
            .ok()
            .body(ErrorResponse.fail(code, message));
    }

    @ExceptionHandler(DuplicatedValueException.class)
    public ResponseEntity<ErrorResponse> handleDuplicatedValueException(
        final DuplicatedValueException e) {
        final int code = e.getErrorCode().getCode();
        final String message = messageSourceAccessor.getMessage(e.getMessage());

        log.info(ERROR_LOG_FORMAT, e.getClass().getSimpleName(), code, message);
        return ResponseEntity
            .ok()
            .body(ErrorResponse.fail(code, message));
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
        return ResponseEntity
            .ok()
            .body(ErrorResponse.fail(code, message));
    }

    @ExceptionHandler(NotEqualRoleToTokenException.class)
    public ResponseEntity<ErrorResponse> handleNotEqualRoleException(
        final NotEqualRoleToTokenException e) {
        final int code = NOT_EQUAL_ROLE_TO_TOKEN.getCode();
        final String message = messageSourceAccessor.getMessage(
            NOT_EQUAL_ROLE_TO_TOKEN.getMessageKey());

        log.info(UNAUTHORIZED_LOG_FORMAT, e.getClass().getSimpleName(), code, message);
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ErrorResponse.fail(code, message));
    }

    // SQL 관련 예외 핸들링 + sql & sql 예외 원인 Logging
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseAccessException(
        final DataAccessException e) {
        final int code = DATA_ACCESS.getCode();

        String message = messageSourceAccessor.getMessage(DATA_ACCESS.getMessageKey());
        if (e.getCause() instanceof JDBCException sqlException) {
            String sql = sqlException.getSQL();
            String sqlMessage = sqlException.getErrorMessage().split("SQL statement:")[0];
            log.warn(SQL_ERROR_LOG_FORMAT, sqlException.getSQLException().getClass().getName(), sql,
                sqlMessage);
        } else {
            log.info(ERROR_LOG_FORMAT, e.getClass().getName(), code, message);
        }

        return ResponseEntity
            .ok()
            .body(ErrorResponse.fail(code, message));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
        final MissingServletRequestParameterException e) {
        final int code = MISSING_REQUEST_PARAMETER.getCode();
        final String message = messageSourceAccessor.getMessage(
            MISSING_REQUEST_PARAMETER.getMessageKey());

        log.info(ERROR_LOG_FORMAT, e.getClass().getName(), code, message);
        return ResponseEntity
            .ok()
            .body(ErrorResponse.fail(code, message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
        final HttpMessageNotReadableException e) {
        final int code = HTTP_MESSAGE_NOT_READABLE.getCode();
        final String message = messageSourceAccessor.getMessage(
            HTTP_MESSAGE_NOT_READABLE.getMessageKey());

        log.info(ERROR_LOG_FORMAT, e.getClass().getName(), code, message);
        return ResponseEntity
            .ok()
            .body(ErrorResponse.fail(code, message));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(final ValidationException e) {
        final int code = VALIDATION_ERROR.getCode();
        final String message = messageSourceAccessor.getMessage(
            VALIDATION_ERROR.getMessageKey());

        log.info(ERROR_LOG_FORMAT + ", causeMessage: {}", e.getClass().getName(), code, message,
            e.getCause().getMessage());
        return ResponseEntity
            .ok()
            .body(ErrorResponse.fail(code, message));
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ErrorResponse> handleInternalServerException(
        final InternalServerException e) {
        final int code = e.getErrorCode().getCode();
        final String message = messageSourceAccessor.getMessage(e.getMessage());

        log.warn(ERROR_LOG_FORMAT, e.getClass().getSimpleName(), code, message);
        return ResponseEntity
            .internalServerError()
            .body(ErrorResponse.error(code, message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(final Exception e) {
        final int code = UNEXPECTED_INTERNAL.getCode();
        final String message = messageSourceAccessor.getMessage(
            UNEXPECTED_INTERNAL.getMessageKey());

        log.error(ERROR_LOG_FORMAT, e.getClass().getName(), code, message);
        return ResponseEntity
            .internalServerError()
            .body(ErrorResponse.error(code, message));
    }
}
