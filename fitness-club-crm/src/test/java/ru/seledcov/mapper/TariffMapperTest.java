package ru.seledcov.mapper;

import org.junit.jupiter.api.Test;
import ru.seledcov.dto.TariffRequestDto;
import ru.seledcov.dto.TariffResponseDto;
import ru.seledcov.entity.Tariff;
import ru.seledcov.infrastructure.AccessTime;
import ru.seledcov.infrastructure.ClientCategory;
import ru.seledcov.infrastructure.SubscriptionType;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TariffMapperTest {

    private final TariffMapper tariffMapper = new TariffMapperImpl();

    @Test
    void shouldMapRequestDtoToTariff() {

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

        Tariff result = tariffMapper.tariffRequestDtoToTariff(dto);

        assertThat(result.getName()).isEqualTo(dto.name());
        assertThat(result.getType()).isEqualTo(dto.type());
        assertThat(result.getClientCategory()).isEqualTo(dto.clientCategory());
        assertThat(result.isUnlimited()).isEqualTo(dto.unlimited());
        assertThat(result.getTotalVisits()).isEqualTo(dto.totalVisits());
        assertThat(result.getDurationDays()).isEqualTo(dto.durationDays());
        assertThat(result.getAccessTime()).isEqualTo(dto.accessTime());
        assertThat(result.getPrice()).isEqualTo(dto.price());

        assertThat(result.isActive()).isFalse();
        assertThat(result.getId()).isNull();
        assertThat(result.getFingerprint()).isNull();
    }

    @Test
    void shouldMapTariffToTariffResponseDto() {

        Tariff tariff = new Tariff();
        tariff.setId(11L);
        tariff.setName("Абонемент 8 посещений");
        tariff.setType(SubscriptionType.SELF_SERVICE);
        tariff.setClientCategory(ClientCategory.ADULT);
        tariff.setUnlimited(false);
        tariff.setTotalVisits(8);
        tariff.setDurationDays(30);
        tariff.setAccessTime(AccessTime.DAY);
        tariff.setPrice(new BigDecimal("3500"));
        tariff.setActive(false);
        tariff.setFingerprint("6d2139281a8f9d6c702d7eb46342886f37cfc8464614e7ee045610586e3f0547");

        TariffResponseDto result = tariffMapper.tariffToTariffResponseDto(tariff);

        assertThat(result.id()).isEqualTo(tariff.getId());
        assertThat(result.name()).isEqualTo(tariff.getName());
        assertThat(result.type()).isEqualTo(tariff.getType());
        assertThat(result.clientCategory()).isEqualTo(tariff.getClientCategory());
        assertThat(result.unlimited()).isEqualTo(tariff.isUnlimited());
        assertThat(result.totalVisits()).isEqualTo(tariff.getTotalVisits());
        assertThat(result.durationDays()).isEqualTo(tariff.getDurationDays());
        assertThat(result.accessTime()).isEqualTo(tariff.getAccessTime());
        assertThat(result.price()).isEqualTo(tariff.getPrice());
        assertThat(result.active()).isEqualTo(tariff.isActive());

    }
}
