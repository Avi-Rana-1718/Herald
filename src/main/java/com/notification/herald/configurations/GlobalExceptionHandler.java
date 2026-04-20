package com.notification.herald.configurations;

import com.notification.herald.dto.ErrorDto;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> exceptionHandler(Exception ex) {
        ErrorDto errorDto = new ErrorDto(ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(errorDto, HttpStatusCode.valueOf(errorDto.status()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> exceptionHandler(MethodArgumentNotValidException ex) {
        String errorMessage = Objects.nonNull(ex.getBindingResult().getFieldError())?ex.getBindingResult().getFieldError().getDefaultMessage():ex.getLocalizedMessage();
        ErrorDto errorDto = new ErrorDto(errorMessage, HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(errorDto, HttpStatusCode.valueOf(errorDto.status()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorDto> exceptionHandler(ResponseStatusException ex) {
        ErrorDto errorDto = new ErrorDto(ex.getMessage(), ex.getStatusCode().value());
        return new ResponseEntity<>(errorDto, HttpStatusCode.valueOf(errorDto.status()));
    }

    @ExceptionHandler(ConstraintViolationException .class)
    public ResponseEntity<ErrorDto> exceptionHandler(ConstraintViolationException ex) {
        ErrorDto errorDto = new ErrorDto(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(errorDto, HttpStatusCode.valueOf(errorDto.status()));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorDto> exceptionHandler(ValidationException ex) {
        ErrorDto errorDto = new ErrorDto(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(errorDto, HttpStatusCode.valueOf(errorDto.status()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorDto> exceptionHandler(MissingServletRequestParameterException ex) {
        ErrorDto errorDto = new ErrorDto(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(errorDto, HttpStatusCode.valueOf(errorDto.status()));
    }
}
