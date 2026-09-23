package ru.seledcov.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
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

    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDto> updateClient(
            @PathVariable @NotNull @Positive Long id,
            @Valid @RequestBody ClientRequestDto dto
    ) {

        ClientResponseDto response = clientService.updateClient(id, dto);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClient(
            @PathVariable @NotNull @Positive Long id
    ) {
        clientService.deleteClient(id);
    }
}
