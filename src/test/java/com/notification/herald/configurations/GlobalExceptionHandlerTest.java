package com.notification.herald.configurations;

import com.notification.herald.dto.ErrorDto;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    GlobalExceptionHandler handler;

    @Mock
    BindingResult bindingResult;

    @Test
    void genericException_returns500() {
        ResponseEntity<ErrorDto> response = handler.exceptionHandler(new RuntimeException("boom"));

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody().data()).isEqualTo("boom");
        assertThat(response.getBody().status()).isEqualTo(500);
    }

    @Test
    void methodArgumentNotValid_withFieldError_returnsBadRequestWithFieldMessage() throws Exception {
        FieldError fieldError = new FieldError("obj", "type", "type is mandatory");
        when(bindingResult.getFieldError()).thenReturn(fieldError);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorDto> response = handler.exceptionHandler(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().data()).isEqualTo("type is mandatory");
    }

    @Test
    void methodArgumentNotValid_noFieldError_returnsLocalizedMessage() throws Exception {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldError()).thenReturn(null);
        when(ex.getLocalizedMessage()).thenReturn("Validation failed");

        ResponseEntity<ErrorDto> response = handler.exceptionHandler(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().data()).isEqualTo("Validation failed");
    }

    @Test
    void responseStatusException_returnsCorrectStatusAndMessage() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");

        ResponseEntity<ErrorDto> response = handler.exceptionHandler(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody().status()).isEqualTo(404);
    }

    @Test
    void constraintViolationException_returns400() {
        ConstraintViolationException ex = new ConstraintViolationException("constraint violated", Set.of());

        ResponseEntity<ErrorDto> response = handler.exceptionHandler(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().data()).contains("constraint violated");
    }

    @Test
    void validationException_returns400() {
        ValidationException ex = new ValidationException("invalid input");

        ResponseEntity<ErrorDto> response = handler.exceptionHandler(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().data()).isEqualTo("invalid input");
    }
}
