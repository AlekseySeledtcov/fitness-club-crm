package ru.seledcov.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.seledcov.dto.ClientResponseDto;
import ru.seledcov.entity.Client;
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
}
