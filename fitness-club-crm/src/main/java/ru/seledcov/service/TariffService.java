package ru.seledcov.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.seledcov.dto.TariffRequestDto;
import ru.seledcov.dto.TariffResponseDto;
import ru.seledcov.entity.Tariff;
import ru.seledcov.exception.TariffAlreadyExistsException;
import ru.seledcov.exception.TariffNotFoundException;
import ru.seledcov.mapper.TariffMapper;
import ru.seledcov.repository.TariffRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TariffService {

    private final TariffValidationService tariffValidationService;
    private final TariffRepository tariffRepository;
    private final TariffMapper tariffMapper;
    private final TariffFingerprintService tariffFingerprintService;

    public TariffResponseDto createTariff(TariffRequestDto tariffRequestDto) {

        tariffValidationService.validateTariff(tariffRequestDto);
        String fingerprint = tariffFingerprintService.calculateFingerprint(tariffRequestDto);

        if (tariffRepository.existsByFingerprint(fingerprint)) {
            throw new TariffAlreadyExistsException(
                    String.format("Tariff with fingerprint '%s' already exists", fingerprint)
            );
        }

        Tariff tariff = tariffMapper.tariffRequestDtoToTariff(tariffRequestDto);
        tariff.setFingerprint(fingerprint);

        return tariffMapper.tariffToTariffResponseDto(tariffRepository.save(tariff));
    }

    public TariffResponseDto getTariffById(Long id) {

        Tariff tariff = tariffRepository.findById(id)
                .orElseThrow(() ->
                        new TariffNotFoundException(String.format("Tariff with id '%d' not found", id)));

        return tariffMapper.tariffToTariffResponseDto(tariff);
    }

    public List<TariffResponseDto> getAllTariffs() {
        return tariffMapper.tariffsToDtos(tariffRepository.findAll());
    }


    @Transactional
    public TariffResponseDto updateTariff(
            Long id,
            TariffRequestDto tariffRequestDto
    ) {
        tariffValidationService.validateTariff(tariffRequestDto);
        Tariff updatedTariff = tariffRepository.findById(id)
                .orElseThrow(() ->
                        new TariffNotFoundException(
                                String.format("Tariff with id '%d' not found", id))
                );
        String fingerprint = tariffFingerprintService.calculateFingerprint(tariffRequestDto);
        if (tariffRepository.existsByFingerprintAndIdNot(fingerprint, id)) {
            throw new TariffAlreadyExistsException(
                    String.format("Tariff with fingerprint '%s' already exists", fingerprint)
            );
        }
        tariffMapper.updateTariffFromTariffRequestDto(
                tariffRequestDto,
                updatedTariff
        );
        updatedTariff.setFingerprint(fingerprint);
        return tariffMapper.tariffToTariffResponseDto(updatedTariff);
    }
}
