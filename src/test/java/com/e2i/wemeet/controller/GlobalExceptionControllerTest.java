package com.e2i.wemeet.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.e2i.wemeet.exception.ErrorCode;
import com.e2i.wemeet.exception.ErrorResponse;
import com.e2i.wemeet.exception.badrequest.InvalidValueException;
import com.e2i.wemeet.exception.internal.InternalServerException;
import com.e2i.wemeet.exception.notfound.NotFoundException;
import com.e2i.wemeet.exception.unauthorized.UnAuthorizedException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingServletRequestParameterException;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionControllerTest {

    @Mock
    private MessageSourceAccessor messageSourceAccessor;

    @InjectMocks
    private GlobalExceptionController globalExceptionController;

    private final String errorMessage = "ERROR";

    @Test
    void handleAuthorizationException() {
        // given
        given(messageSourceAccessor.getMessage(anyString()))
            .willReturn(errorMessage);

        // when
        ResponseEntity<ErrorResponse> response = globalExceptionController.handleAuthorizationException(
            new AccessDeniedException("Access Denied"));

        // then
        Assertions.assertThat(response.getBody()).isExactlyInstanceOf(ErrorResponse.class);
    }

    @Test
    void handleInvalidValueException() {
        // given
        given(messageSourceAccessor.getMessage(anyString()))
            .willReturn(errorMessage);

        // when
        ResponseEntity<ErrorResponse> response = globalExceptionController.handleInvalidValueException(
            new InvalidValueException(ErrorCode.INVALID_DATA_FORMAT));

        // then
        Assertions.assertThat(response.getBody()).isExactlyInstanceOf(ErrorResponse.class);
    }

    @Test
    void handleNotFoundException() {
        // given
        given(messageSourceAccessor.getMessage(anyString()))
            .willReturn(errorMessage);

        // when
        ResponseEntity<ErrorResponse> response = globalExceptionController.handleNotFoundException(
            new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        // then
        Assertions.assertThat(response.getBody()).isExactlyInstanceOf(ErrorResponse.class);
    }

    @Test
    void handleUnAuthorizedException() {
        // given
        given(messageSourceAccessor.getMessage(anyString()))
            .willReturn(errorMessage);

        // when
        ResponseEntity<ErrorResponse> response = globalExceptionController.handleUnAuthorizedException(
            new UnAuthorizedException(ErrorCode.UNAUTHORIZED));

        // then
        Assertions.assertThat(response.getBody()).isExactlyInstanceOf(ErrorResponse.class);
    }

    @Test
    void handleInternalServerException() {
        // given
        given(messageSourceAccessor.getMessage(anyString()))
            .willReturn(errorMessage);

        // when
        ResponseEntity<ErrorResponse> response = globalExceptionController.handleInternalServerException(
            new InternalServerException(ErrorCode.AWS_S3_OBJECT_DELETE_ERROR));

        // then
        Assertions.assertThat(response.getBody()).isExactlyInstanceOf(ErrorResponse.class);
    }

    @Test
    void handleMissingServletRequestParameterException() {
        // given
        given(messageSourceAccessor.getMessage(anyString()))
            .willReturn(errorMessage);

        // when
        ResponseEntity<ErrorResponse> response = globalExceptionController.handleMissingServletRequestParameterException(
            new MissingServletRequestParameterException("param", "String"));

        // then
        Assertions.assertThat(response.getBody()).isExactlyInstanceOf(ErrorResponse.class);
    }

    @Test
    void handleUnexpectedException() {
        // given
        given(messageSourceAccessor.getMessage(anyString()))
            .willReturn(errorMessage);

        // when
        ResponseEntity<ErrorResponse> response = globalExceptionController.handleUnexpectedException(
            new Exception("Unexpected Exception"));

        // then
        Assertions.assertThat(response.getBody()).isExactlyInstanceOf(ErrorResponse.class);
    }
}