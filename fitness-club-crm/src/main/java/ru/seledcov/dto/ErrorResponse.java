package ru.seledcov.dto;

public record ErrorResponse(
        int status,
        String message
) {
}
