package ru.seledcov.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.seledcov.dto.ClientRequestDto;
import ru.seledcov.dto.ClientResponseDto;
import ru.seledcov.entity.Client;
import ru.seledcov.exception.EmailAlreadyExistsException;
import ru.seledcov.mapper.ClientMapper;
import ru.seledcov.repository.ClientRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    public List<ClientResponseDto> getClients() {
        return clientMapper.clientsToDto(clientRepository.findAll());
    }

    public ClientResponseDto createClient(ClientRequestDto clientRequestDto) {

        String email = clientRequestDto.email();
        if (clientRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(
                    String.format("Client with email '%s' already exists", email)
            );
        }

        Client client = clientMapper.clientRequestDtoToClient(clientRequestDto);

        Client savedClient = clientRepository.save(client);

        return clientMapper.clientToDto(savedClient);
    }
}
