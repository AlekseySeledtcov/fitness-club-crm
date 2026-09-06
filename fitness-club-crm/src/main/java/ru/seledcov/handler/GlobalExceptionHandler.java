package ru.seledcov.handler;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.seledcov.dto.ErrorResponse;
import ru.seledcov.exception.EmailAlreadyExistsException;
import ru.seledcov.infrastructure.DatabaseConstraint;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Pattern CONSTRAINT_PATTERN =
            Pattern.compile("unique constraint \"([^\"]+)\"");

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> emailAlreadyExistsExceptionHandler(EmailAlreadyExistsException exception) {

        HttpStatus status = HttpStatus.CONFLICT;

        ErrorResponse response = new ErrorResponse(
                status.value(),
                exception.getMessage()
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> dataIntegrityViolationExceptionHandler(
            DataIntegrityViolationException exception
    ) {

        String constraintName = extractConstraintName(exception);
        String message = "Database constraint violation";
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        switch (DatabaseConstraint.fromConstraintName(constraintName)) {
            case UK_CLIENT_EMAIL -> {
                message = "Client with email already exists";
                status = HttpStatus.CONFLICT;
            }
            case UK_CLIENT_PHONE -> {
                message = "Client with phone already exists";
                status = HttpStatus.CONFLICT;
            }
        }

        ErrorResponse response = new ErrorResponse(
                status.value(),
                message
        );

        return ResponseEntity.status(status).body(response);
    }

    private String extractConstraintName(DataIntegrityViolationException exception) {
        Throwable rootCause = exception.getRootCause();
        if (rootCause == null || rootCause.getMessage() == null) {
            return "unknown";
        }

        String postgresMessage = rootCause.getMessage();
        Matcher matcher = CONSTRAINT_PATTERN.matcher(postgresMessage);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "unknown";
    }
}
