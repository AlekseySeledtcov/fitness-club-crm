package ru.seledcov.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ru.seledcov.infrastructure.AccessTime;
import ru.seledcov.infrastructure.ClientCategory;
import ru.seledcov.infrastructure.SubscriptionType;

import java.math.BigDecimal;

public record TariffRequestDto(
        @NotBlank
        String name,

        @NotNull
        SubscriptionType type,

        @NotNull
        ClientCategory clientCategory,

        boolean unlimited,

        @Positive
        Integer totalVisits,

        @NotNull
        @Positive
        Integer durationDays,

        AccessTime accessTime,

        @NotNull
        @Positive
        @Digits(integer = 12, fraction = 2)
        BigDecimal price
) {
}
