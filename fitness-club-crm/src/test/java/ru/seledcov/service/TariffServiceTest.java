package ru.seledcov.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.seledcov.dto.TariffRequestDto;
import ru.seledcov.dto.TariffResponseDto;
import ru.seledcov.entity.Tariff;
import ru.seledcov.exception.TariffAlreadyExistsException;
import ru.seledcov.exception.TariffNotFoundException;
import ru.seledcov.infrastructure.AccessTime;
import ru.seledcov.infrastructure.ClientCategory;
import ru.seledcov.infrastructure.SubscriptionType;
import ru.seledcov.mapper.TariffMapper;
import ru.seledcov.repository.TariffRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TariffServiceTest {

    @Mock
    private TariffValidationService tariffValidationService;

    @Mock
    private TariffRepository tariffRepository;

    @Mock
    private TariffMapper tariffMapper;

    @Mock
    private TariffFingerprintService tariffFingerprintService;

    @InjectMocks
    private TariffService tariffService;

    @Captor
    private ArgumentCaptor<Tariff> tariffCaptor;

    @Test
    void shouldCreateTariffWithCalculatedFingerprint() {

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
        String fingerprint = "6d2139281a8f9d6c702d7eb46342886f37cfc8464614e7ee045610586e3f0547";

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
        tariff.setFingerprint(fingerprint);

        TariffResponseDto tariffResponseDto = new TariffResponseDto(
                11L,
                "Абонемент 8 посещений",
                SubscriptionType.SELF_SERVICE,
                ClientCategory.ADULT,
                false,
                8,
                30,
                AccessTime.DAY,
                new BigDecimal("3500"),
                false
        );

        when(tariffFingerprintService.calculateFingerprint(dto))
                .thenReturn(fingerprint);
        when(tariffRepository.existsByFingerprint(fingerprint))
                .thenReturn(false);
        when(tariffMapper.tariffRequestDtoToTariff(dto))
                .thenReturn(tariff);
        when(tariffRepository.save(tariff))
                .thenReturn(tariff);
        when(tariffMapper.tariffToTariffResponseDto(tariff))
                .thenReturn(tariffResponseDto);

        TariffResponseDto result = tariffService.createTariff(dto);
        verify(tariffFingerprintService).calculateFingerprint(dto);
        verify(tariffRepository).existsByFingerprint(fingerprint);
        verify(tariffMapper).tariffRequestDtoToTariff(dto);
        verify(tariffRepository).save(tariffCaptor.capture());
        verify(tariffMapper).tariffToTariffResponseDto(tariff);
        Tariff savedTariff = tariffCaptor.getValue();

        assertThat(savedTariff.getFingerprint()).isEqualTo(fingerprint);
        assertThat(result).isEqualTo(tariffResponseDto);
    }

    @Test
    void shouldThrowTariffAlreadyExistsExceptionWhenCreatedTariffAlreadyExists() {

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

        String fingerprint = "6d2139281a8f9d6c702d7eb46342886f37cfc8464614e7ee045610586e3f0547";

        when(tariffFingerprintService.calculateFingerprint(dto1))
                .thenReturn(fingerprint);
        when(tariffRepository.existsByFingerprint(fingerprint))
                .thenReturn(true);

        assertThatThrownBy(() ->
                tariffService.createTariff(dto1))
                .isInstanceOf(TariffAlreadyExistsException.class)
                .hasMessage(String.format("Tariff with fingerprint '%s' already exists", fingerprint));
        verify(tariffFingerprintService).calculateFingerprint(dto1);
        verify(tariffValidationService).validateTariff(dto1);
        verify(tariffMapper, never()).tariffRequestDtoToTariff(dto1);
        verify(tariffRepository, never()).save(any(Tariff.class));
        verify(tariffMapper, never()).tariffToTariffResponseDto(any(Tariff.class));
    }

    @Test
    void shouldReturnTariffResponseDtoWhenTariffExists() {

        TariffResponseDto tariffResponseDto = new TariffResponseDto(
                11L,
                "Абонемент 8 посещений",
                SubscriptionType.SELF_SERVICE,
                ClientCategory.ADULT,
                false,
                8,
                30,
                AccessTime.DAY,
                new BigDecimal("3500"),
                false
        );

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

        Long tariffId = tariff.getId();

        when(tariffRepository.findById(tariffId))
                .thenReturn(Optional.of(tariff));
        when(tariffMapper.tariffToTariffResponseDto(tariff))
                .thenReturn(tariffResponseDto);

        TariffResponseDto result = tariffService.getTariffById(tariffId);

        verify(tariffRepository).findById(tariffId);
        verify(tariffMapper).tariffToTariffResponseDto(tariff);
        assertThat(result).isEqualTo(tariffResponseDto);
    }

    @Test
    void shouldThrowTariffNotFoundExceptionWhenTariffNotFound() {

        Long tariffId = 11L;

        when(tariffRepository.findById(tariffId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                tariffService.getTariffById(tariffId))
                .isInstanceOf(TariffNotFoundException.class)
                .hasMessage(String.format("Tariff with id '%d' not found", tariffId));
        verify(tariffMapper, never())
                .tariffToTariffResponseDto(any(Tariff.class));

    }

    @Test
    void shouldReturnListTariffResponseDtosWhenTariffsExists() {
        TariffResponseDto tariffResponseDto = new TariffResponseDto(
                11L,
                "Абонемент 8 посещений",
                SubscriptionType.SELF_SERVICE,
                ClientCategory.ADULT,
                false,
                8,
                30,
                AccessTime.DAY,
                new BigDecimal("3500"),
                false
        );

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

        when(tariffRepository.findAll())
                .thenReturn(List.of(tariff));
        when(tariffMapper.tariffsToDtos(List.of(tariff)))
                .thenReturn(List.of(tariffResponseDto));

        List<TariffResponseDto> result = tariffService.getAllTariffs();

        assertThat(result.getFirst()).isEqualTo(tariffResponseDto);
        verify(tariffRepository).findAll();
        verify(tariffMapper).tariffsToDtos(List.of(tariff));
    }

    @Test
    void shouldUpdateTariffSuccessfully() {
        Long id = 11L;
        TariffRequestDto dto = new TariffRequestDto(
                "Абонемент 8 посещений",
                SubscriptionType.SELF_SERVICE,
                ClientCategory.ADULT,
                false,
                8,
                30,
                AccessTime.DAY,
                new BigDecimal("4000")
        );
        String oldFingerprint = "6d2139281a8f9d6c702d7eb46342886f37cfc8464614e7ee045610586e3f0547";
        String newFingerprint = "6d2139281a8f9d6c702d7eb46342886f37cfc8464614e7ee045610586e3f0548";
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
        tariff.setFingerprint(oldFingerprint);

        TariffResponseDto tariffResponseDto = new TariffResponseDto(
                11L,
                "Абонемент 8 посещений",
                SubscriptionType.SELF_SERVICE,
                ClientCategory.ADULT,
                false,
                8,
                30,
                AccessTime.DAY,
                new BigDecimal("4000"),
                false
        );

        when(tariffRepository.findById(id))
                .thenReturn(Optional.of(tariff));
        when(tariffFingerprintService.calculateFingerprint(dto))
                .thenReturn(newFingerprint);
        when(tariffRepository.existsByFingerprintAndIdNot(newFingerprint, id))
                .thenReturn(false);
        when(tariffMapper.tariffToTariffResponseDto(tariff))
                .thenReturn(tariffResponseDto);

        TariffResponseDto result = tariffService.updateTariff(id, dto);

        assertThat(result).isEqualTo(tariffResponseDto);
        assertThat(tariff.getFingerprint()).isEqualTo(newFingerprint);
        verify(tariffMapper).updateTariffFromTariffRequestDto(dto, tariff);
        verify(tariffRepository).existsByFingerprintAndIdNot(newFingerprint, id);
        verify(tariffRepository, never()).save(any(Tariff.class));
    }

    @Test
    void shouldThrowTariffNotFoundExceptionWhenUpdatingTariffNotFound() {
        Long id = 99L;
        TariffRequestDto dto = new TariffRequestDto(
                "Абонемент 8 посещений",
                SubscriptionType.SELF_SERVICE,
                ClientCategory.ADULT,
                false,
                8,
                30,
                AccessTime.DAY,
                new BigDecimal("4000")
        );

        when(tariffRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                tariffService.updateTariff(id, dto))
                .isInstanceOf(TariffNotFoundException.class)
                .hasMessage(String.format("Tariff with id '%d' not found", id));

        verify(tariffRepository).findById(id);
        verify(tariffFingerprintService, never()).calculateFingerprint(dto);
        verify(tariffRepository, never()).existsByFingerprintAndIdNot(any(String.class), eq(id));
        verify(tariffMapper, never()).updateTariffFromTariffRequestDto(eq(dto), any(Tariff.class));
        verify(tariffMapper, never()).tariffToTariffResponseDto(any(Tariff.class));
    }

    @Test
    void shouldThrowTariffAlreadyExistsExceptionWhenUpdatingTariffAlreadyExists() {
        Long id = 11L;
        TariffRequestDto dto = new TariffRequestDto(
                "Абонемент 8 посещений",
                SubscriptionType.SELF_SERVICE,
                ClientCategory.ADULT,
                false,
                8,
                30,
                AccessTime.DAY,
                new BigDecimal("4000")
        );
        String oldFingerprint = "6d2139281a8f9d6c702d7eb46342886f37cfc8464614e7ee045610586e3f0547";
        String newFingerprint = "6d2139281a8f9d6c702d7eb46342886f37cfc8464614e7ee045610586e3f0548";
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
        tariff.setFingerprint(oldFingerprint);

        when(tariffRepository.findById(id))
                .thenReturn(Optional.of(tariff));
        when(tariffFingerprintService.calculateFingerprint(dto))
                .thenReturn(newFingerprint);
        when(tariffRepository.existsByFingerprintAndIdNot(newFingerprint, id))
                .thenReturn(true);

        assertThatThrownBy(() ->
                tariffService.updateTariff(id, dto))
                .isInstanceOf(TariffAlreadyExistsException.class)
                .hasMessage(String.format("Tariff with fingerprint '%s' already exists", newFingerprint));

        verify(tariffValidationService).validateTariff(dto);
        verify(tariffRepository).findById(id);
        verify(tariffFingerprintService).calculateFingerprint(dto);
        verify(tariffRepository).existsByFingerprintAndIdNot(newFingerprint, id);
        verify(tariffMapper, never()).updateTariffFromTariffRequestDto(dto, tariff);
        verify(tariffMapper, never()).tariffToTariffResponseDto(tariff);
    }
}
