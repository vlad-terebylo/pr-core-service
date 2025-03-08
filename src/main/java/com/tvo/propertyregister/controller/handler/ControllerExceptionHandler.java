package com.tvo.propertyregister.controller.handler;

import com.tvo.propertyregister.exception.*;
import com.tvo.propertyregister.model.dto.ErrorDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception exception, WebRequest request) {
        ErrorDto error = new ErrorDto(INTERNAL_SERVER_ERROR.getReasonPhrase(), exception.getMessage());

        return super.handleExceptionInternal(exception, error, new HttpHeaders(), INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(value = DontHaveTaxDebtsException.class)
    public ResponseEntity<Object> handleDontHaveTaxDebtsException(Exception exception, WebRequest request) {
        ErrorDto error = new ErrorDto(BAD_REQUEST.getReasonPhrase(), exception.getMessage());

        return super.handleExceptionInternal(exception, error, new HttpHeaders(), BAD_REQUEST, request);
    }

    @ExceptionHandler(value = NoSuchOwnerException.class)
    public ResponseEntity<Object> handleNoSuchOwnerException(Exception exception, WebRequest request) {
        ErrorDto error = new ErrorDto(NOT_FOUND.getReasonPhrase(), exception.getMessage());

        return super.handleExceptionInternal(exception, error, new HttpHeaders(), NOT_FOUND, request);
    }

    @ExceptionHandler(value = UpdateOwnerFailedException.class)
    public ResponseEntity<Object> handleUpdatePropertyFailedException(Exception exception, WebRequest request) {
        ErrorDto error = new ErrorDto(BAD_REQUEST.getReasonPhrase(), exception.getMessage());

        return super.handleExceptionInternal(exception, error, new HttpHeaders(), BAD_REQUEST, request);
    }

    @ExceptionHandler(value = PropertyTypeDoesNotExistException.class)
    public ResponseEntity<Object> handlePropertyTypeDoesNotExistException(Exception exception, WebRequest request) {
        ErrorDto error = new ErrorDto(NOT_FOUND.getReasonPhrase(), exception.getMessage());

        return super.handleExceptionInternal(exception, error, new HttpHeaders(), NOT_FOUND, request);
    }

    @ExceptionHandler(value = NoDebtorsInDebtorListException.class)
    public ResponseEntity<Object> handleNoDebtorsInDebtorListException(Exception exception, WebRequest request) {
        ErrorDto error = new ErrorDto(NO_CONTENT.getReasonPhrase(), exception.getMessage());

        return super.handleExceptionInternal(exception, error, new HttpHeaders(), NO_CONTENT, request);
    }
}
