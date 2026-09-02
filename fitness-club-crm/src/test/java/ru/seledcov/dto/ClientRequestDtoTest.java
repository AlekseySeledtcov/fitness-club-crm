package ru.seledcov.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Set;

public class ClientRequestDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation
                .buildDefaultValidatorFactory()
                .getValidator();
    }

    @Test
    void shouldFailValidation_WhenFirstNameIsBlank() {
        ClientRequestDto clientRequestDto = new ClientRequestDto(
                "",
                "Ivanov",
                "example@mail.com",
                "+7 999 999 99 99",
                LocalDate.of(1990, 5, 15)
        );

        Set<ConstraintViolation<ClientRequestDto>> violations = validator.validate(clientRequestDto);

        assertThat(violations)
                .anyMatch(violation ->
                        violation.getPropertyPath().toString().equals("firstName")
                                && violation.getMessage().equals("First name cannot be blank"));
    }

    @Test
    void shouldPassValidation_WhenPhoneIsNull() {
        ClientRequestDto clientRequestDto = new ClientRequestDto(
                "Ivan",
                "Ivanov",
                "example@mail.com",
                null,
                LocalDate.of(1990, 5, 15)
        );

        Set<ConstraintViolation<ClientRequestDto>> violations = validator.validate(clientRequestDto);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailValidation_WhenPhoneHasInvalidFormat(){
        ClientRequestDto clientRequestDto = new ClientRequestDto (
                "Ivan",
                "Ivanov",
                "example@mail.com",
                "8 999 999 99 99",
                LocalDate.of(1990, 5, 15)
        );

        Set<ConstraintViolation<ClientRequestDto>> violations = validator.validate(clientRequestDto);

        assertThat(violations)
                .anyMatch(violation ->
                violation.getPropertyPath().toString().equals("phone")
                && violation.getMessage().equals("Phone number must be in the format: +7 999 999 99 99"));
    }

    @Test
    void shouldPassValidation_WhenBirthDateNull() {
        ClientRequestDto clientRequestDto = new ClientRequestDto(
                "Ivan",
                "Ivanov",
                "example@mail.com",
                "+7 999 999 99 99",
                null
        );

        Set<ConstraintViolation<ClientRequestDto>> violations = validator.validate(clientRequestDto);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailValidation_WhenBirthDateIsInFuture() {
        ClientRequestDto clientRequestDto = new ClientRequestDto(
                "Ivan",
                "Ivanov",
                "example@mail.com",
                "+7 999 999 99 99",
                LocalDate.now().plusDays(1)
        );

        Set<ConstraintViolation<ClientRequestDto>> violations = validator.validate(clientRequestDto);

        assertThat(violations)
                .anyMatch(violation ->
                        violation.getPropertyPath().toString().equals("birthDate")
                && violation.getMessage().equals("Date of birth must be in the past"));
    }

}
