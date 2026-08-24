package ru.seledcov.dto;

import java.time.LocalDate;

public record ClientResponseDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        LocalDate birthDate
) {

}