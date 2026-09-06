package ru.seledcov.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.seledcov.dto.ErrorResponse;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    DataIntegrityViolationException exception;

    @Mock
    private Throwable rootCause;

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void shouldReturnConflict_WhenEmailConstraintIsViolated() {
        when(rootCause.getMessage())
                .thenReturn("ERROR: duplicate key value violates unique constraint \"uk_client_email\"");
        when(exception.getRootCause())
                .thenReturn(rootCause);

        ResponseEntity<ErrorResponse> response =
                globalExceptionHandler.dataIntegrityViolationExceptionHandler(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().status()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(response.getBody().message()).isEqualTo("Client with email already exists");
    }

    @Test
    void shouldReturnConflict_WhenPhoneConstraintIsViolated() {
        when(rootCause.getMessage())
                .thenReturn("ERROR: duplicate key value violates unique constraint \"uk_client_phone\"");
        when(exception.getRootCause())
                .thenReturn(rootCause);

        ResponseEntity<ErrorResponse> response =
                globalExceptionHandler.dataIntegrityViolationExceptionHandler(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().status()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(response.getBody().message()).isEqualTo("Client with phone already exists");
    }

    @Test
    void shouldReturnInternalServerError_WhenUnknownConstraintIsViolated() {
        when(rootCause.getMessage())
                .thenReturn("ERROR: duplicate key value violates unique constraint \"uk_client_membership_id\"");
        when(exception.getRootCause())
                .thenReturn(rootCause);

        ResponseEntity<ErrorResponse> response =
                globalExceptionHandler.dataIntegrityViolationExceptionHandler(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().status()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.getBody().message()).isEqualTo("Database constraint violation");
    }
}
