package ru.seledcov.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.seledcov.dto.TariffRequestDto;
import ru.seledcov.dto.TariffResponseDto;
import ru.seledcov.service.TariffService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/app/v1/tariffs")
public class TariffController {

    private final TariffService tariffService;

    @PostMapping
    public ResponseEntity<TariffResponseDto> createTariff(
            @RequestBody
            @Valid
            TariffRequestDto tariffRequestDto
    ) {

        TariffResponseDto body = tariffService.createTariff(tariffRequestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TariffResponseDto> getTariffById(
            @PathVariable
            @NotNull
            @Positive
            Long id
    ) {

        TariffResponseDto body = tariffService.getTariffById(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(body);
    }

    @GetMapping
    public ResponseEntity<List<TariffResponseDto>> getAllTariffs() {

        List<TariffResponseDto> body = tariffService.getAllTariffs();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(body);
    }
}
