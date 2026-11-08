package ru.seledcov.service;

import org.springframework.stereotype.Service;
import ru.seledcov.dto.TariffRequestDto;
import ru.seledcov.infrastructure.SubscriptionType;

@Service
public class TariffValidationService {

    public void validateTariff(TariffRequestDto dto) {

        if (dto.type() == SubscriptionType.SELF_SERVICE &&
                dto.accessTime() == null) {
            throw new IllegalArgumentException(
                    "Access time is required for self-service tariffs"
            );
        }

        if (dto.type() != SubscriptionType.SELF_SERVICE &&
                dto.accessTime() != null) {
            throw new IllegalArgumentException(
                    "Access time is allowed only for self-service tariffs"
            );
        }

        if (!dto.unlimited() && dto.totalVisits() == null) {
            throw new IllegalArgumentException(
                    "Total visits are required for non-unlimited tariffs"
            );
        }

        if (dto.unlimited() && dto.totalVisits() != null) {
            throw new IllegalArgumentException(
                    "For an unlimited tariff, the number of visits should not be specified"
            );
        }
    }

}
