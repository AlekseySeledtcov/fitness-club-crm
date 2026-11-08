package ru.seledcov.service;

import org.junit.jupiter.api.Test;
import ru.seledcov.dto.TariffRequestDto;
import ru.seledcov.infrastructure.AccessTime;
import ru.seledcov.infrastructure.ClientCategory;
import ru.seledcov.infrastructure.SubscriptionType;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class TariffValidationServiceTest {

    private final TariffValidationService tariffValidationService =
            new TariffValidationService();

    @Test
    void shouldThrowExceptionWhenSelfServiceTariffHasNoAccessTime() {

        TariffRequestDto tariffRequestDto = new TariffRequestDto(
                "Самостоятельное заняти",
                SubscriptionType.SELF_SERVICE,
                ClientCategory.ADULT,
                false,
                12,
                30,
                null,
                BigDecimal.valueOf(4000)
        );

        assertThatThrownBy(
                () -> tariffValidationService.validateTariff(tariffRequestDto)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Access time is required for self-service tariffs");
    }

    @Test
    void shouldThrowExceptionWhenNonSelfServiceTariffHasAccessTime() {

        TariffRequestDto tariffRequestDto = new TariffRequestDto(
                "Персональное занятие",
                SubscriptionType.PERSONAL,
                ClientCategory.ADULT,
                false,
                12,
                30,
                AccessTime.DAY,
                BigDecimal.valueOf(4000)
        );

        assertThatThrownBy(
                () -> tariffValidationService.validateTariff(tariffRequestDto)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Access time is allowed only for self-service tariffs");

    }

    @Test
    void shouldThrowExceptionWhenNonUnlimitedTariffHasNoTotalVisits() {

        TariffRequestDto tariffRequestDto = new TariffRequestDto(
                "Самостоятельное заняти",
                SubscriptionType.SELF_SERVICE,
                ClientCategory.ADULT,
                false,
                null,
                30,
                AccessTime.DAY,
                BigDecimal.valueOf(4000)
        );

        assertThatThrownBy(
                () -> tariffValidationService.validateTariff(tariffRequestDto)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Total visits are required for non-unlimited tariffs");
    }

    @Test
    void shouldThrowExceptionWhenUnlimitedTariffHasTotalVisits() {

        TariffRequestDto tariffRequestDto = new TariffRequestDto(
                "Самостоятельное заняти",
                SubscriptionType.SELF_SERVICE,
                ClientCategory.ADULT,
                true,
                8,
                30,
                AccessTime.DAY,
                BigDecimal.valueOf(4000)
        );

        assertThatThrownBy(
                () -> tariffValidationService.validateTariff(tariffRequestDto)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("For an unlimited tariff, the number of visits should not be specified");
    }

    @Test
    void shouldValidateCorrectTariff() {

        TariffRequestDto tariffRequestDto = new TariffRequestDto(
                "Самостоятельное заняти",
                SubscriptionType.SELF_SERVICE,
                ClientCategory.ADULT,
                false,
                8,
                60,
                AccessTime.DAY,
                BigDecimal.valueOf(5000)
        );

        assertThatCode(
                () -> tariffValidationService.validateTariff(tariffRequestDto)
        )
                .doesNotThrowAnyException();
    }

}
