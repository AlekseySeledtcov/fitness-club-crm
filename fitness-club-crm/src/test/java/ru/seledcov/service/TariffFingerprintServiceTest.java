package ru.seledcov.service;

import org.junit.jupiter.api.Test;
import ru.seledcov.dto.TariffRequestDto;
import ru.seledcov.infrastructure.AccessTime;
import ru.seledcov.infrastructure.ClientCategory;
import ru.seledcov.infrastructure.SubscriptionType;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class TariffFingerprintServiceTest {

    private final TariffFingerprintService tariffFingerprintService = new TariffFingerprintService();

    @Test
    void shouldReturnSameFingerprintForEqualTariffs() {

        TariffRequestDto dto = new TariffRequestDto(
                "Абонемент 8 посещений",
                SubscriptionType.SELF_SERVICE,
                ClientCategory.ADULT,
                false,
                8,
                30,
                AccessTime.DAY,
                new BigDecimal("3500.0")
        );

        String result1 = tariffFingerprintService.calculateFingerprint(dto);
        String result2 = tariffFingerprintService.calculateFingerprint(dto);

        assertThat(result1).isEqualTo(result2);
    }

    @Test
    void shouldReturnDifferentFingerprintWhenTariffDataDiffers() {

        TariffRequestDto dto1 = new TariffRequestDto(
                "Абонемент 8 посещений",
                SubscriptionType.SELF_SERVICE,
                ClientCategory.ADULT,
                false,
                8,
                30,
                AccessTime.DAY,
                new BigDecimal("3500.0")
        );

        TariffRequestDto dto2 = new TariffRequestDto(
                "Абонемент 8 посещений",
                SubscriptionType.SELF_SERVICE,
                ClientCategory.ADULT,
                false,
                12,
                30,
                AccessTime.DAY,
                new BigDecimal("3500.0")
        );

        String result1 = tariffFingerprintService.calculateFingerprint(dto1);
        String result2 = tariffFingerprintService.calculateFingerprint(dto2);

        assertThat(result1).isNotEqualTo(result2);
    }

    @Test
    void shouldReturnSameFingerprintForNamesWithDifferentFormatting() {

        TariffRequestDto dto1 = new TariffRequestDto(
                "Абонемент 8 посещений",
                SubscriptionType.SELF_SERVICE,
                ClientCategory.ADULT,
                false,
                8,
                30,
                AccessTime.DAY,
                new BigDecimal("3500.0")
        );
        TariffRequestDto dto2 = new TariffRequestDto(
                " АБОНЕМЕНТ 8 ПОСЕЩЕНИЙ ",
                SubscriptionType.SELF_SERVICE,
                ClientCategory.ADULT,
                false,
                8,
                30,
                AccessTime.DAY,
                new BigDecimal("3500.0")
        );

        String result1 = tariffFingerprintService.calculateFingerprint(dto1);
        String result2 = tariffFingerprintService.calculateFingerprint(dto2);

        assertThat(result1).isEqualTo(result2);
    }

    @Test
    void shouldReturnSameFingerprintForEquivalentPrices() {

        TariffRequestDto dto1 = new TariffRequestDto(
                "Абонемент 8 посещений",
                SubscriptionType.SELF_SERVICE,
                ClientCategory.ADULT,
                false,
                8,
                30,
                AccessTime.DAY,
                new BigDecimal("3500")
        );
        TariffRequestDto dto2 = new TariffRequestDto(
                "Абонемент 8 посещений",
                SubscriptionType.SELF_SERVICE,
                ClientCategory.ADULT,
                false,
                8,
                30,
                AccessTime.DAY,
                new BigDecimal("3500.00")
        );

        String result1 = tariffFingerprintService.calculateFingerprint(dto1);
        String result2 = tariffFingerprintService.calculateFingerprint(dto2);

        assertThat(result1).isEqualTo(result2);
    }

    @Test
    void shouldReturnSha256FingerprintInHexFormat() {

        TariffRequestDto dto = new TariffRequestDto(
                "Абонемент 8 посещений",
                SubscriptionType.SELF_SERVICE,
                ClientCategory.ADULT,
                false,
                8,
                30,
                AccessTime.DAY,
                new BigDecimal("3500")
        );

        String result = tariffFingerprintService.calculateFingerprint(dto);

        assertThat(result).hasSize(64);
        assertThat(result).matches("^[0-9a-f]+$");
    }
}
