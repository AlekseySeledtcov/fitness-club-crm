package ru.seledcov.dto;

import jakarta.validation.constraints.NotNull;
import ru.seledcov.infrastructure.AccessTime;
import ru.seledcov.infrastructure.ClientCategory;
import ru.seledcov.infrastructure.SubscriptionType;

import java.math.BigDecimal;

public record TariffResponseDto(
        Long id,
        String name,
        @NotNull
        SubscriptionType type,
        ClientCategory clientCategory,
        boolean unlimited,
        Integer totalVisits,
        Integer durationDays,
        AccessTime accessTime,
        BigDecimal price,
        boolean active
) {
}
