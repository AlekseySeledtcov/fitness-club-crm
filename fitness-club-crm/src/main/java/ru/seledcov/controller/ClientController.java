package ru.seledcov.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.seledcov.dto.ClientRequestDto;
import ru.seledcov.dto.ClientResponseDto;
import ru.seledcov.service.ClientService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/clients")
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    public List<ClientResponseDto> getClients() {
        return clientService.getClients();
    }

    @PostMapping
    public ResponseEntity<ClientResponseDto> createClient(
            @RequestBody
            @Valid
            ClientRequestDto clientRequestDto
    ) {

        ClientResponseDto body = clientService.createClient(clientRequestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDto> getClientById(
            @PathVariable
            @Positive
            Long id
    ) {

        ClientResponseDto response = clientService.getClientById(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
