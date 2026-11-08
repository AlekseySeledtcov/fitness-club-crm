package ru.seledcov.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.seledcov.dto.TariffRequestDto;
import ru.seledcov.dto.TariffResponseDto;
import ru.seledcov.entity.Tariff;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TariffMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "false")
    @Mapping(target = "fingerprint", ignore = true)
    Tariff tariffRequestDtoToTariff(TariffRequestDto dto);

    TariffResponseDto tariffToTariffResponseDto(Tariff tariff);

    List<TariffResponseDto> tariffsToDtos(List<Tariff> tariffs);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "fingerprint", ignore = true)
    void updateTariffFromTariffRequestDto(
            TariffRequestDto dto,
            @MappingTarget Tariff tariff
    );
}
