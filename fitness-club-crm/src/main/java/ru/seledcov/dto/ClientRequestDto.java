package ru.seledcov.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record ClientRequestDto(

        @NotBlank(message = "First name cannot be blank")
        @Size(max = 100, message = "Must not exceed {max} characters")
        String firstName,

        @NotBlank(message = "Last name cannot be blank")
        @Size(max = 100, message = "Must not exceed {max} characters")
        String lastName,

        @NotBlank(message = "Email address is required")
        @Email(message = "Please enter a valid email address")
        @Size(max = 255, message = "Must not exceed {max} characters")
        String email,

        @Size(max = 30, message = "Must not exceed {max} characters")
        @Pattern(
                regexp = "^\\+7 \\d{3} \\d{3} \\d{2} \\d{2}$",
                message = "Phone number must be in the format: +7 999 999 99 99"
        )
        String phone,

        @Past(message = "Date of birth must be in the past")
        LocalDate birthDate
) {
}
