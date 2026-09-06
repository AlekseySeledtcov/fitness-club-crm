package ru.seledcov.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.seledcov.dto.ClientRequestDto;
import ru.seledcov.dto.ClientResponseDto;
import ru.seledcov.entity.Client;
import ru.seledcov.exception.EmailAlreadyExistsException;
import ru.seledcov.mapper.ClientMapper;
import ru.seledcov.repository.ClientRepository;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientService clientService;

    private Client client;
    private ClientResponseDto clientResponseDto;
    private ClientRequestDto clientRequestDto;

    @BeforeEach
    void setUp() {

        client = new Client();
        client.setId(11L);
        client.setFirstName("Ivan");
        client.setLastName("Ivanov");
        client.setEmail("example@mail.com");
        client.setPhone("+7 999 999 99 99");
        client.setBirthDate(LocalDate.of(1990, 5, 15));

        clientResponseDto = new ClientResponseDto(
                11L,
                "Ivan",
                "Ivanov",
                "example@mail.com",
                "+7 999 999 99 99",
                LocalDate.of(1990, 5, 15)

        );

        clientRequestDto = new ClientRequestDto(
                "Ivan",
                "Ivanov",
                "example@mail.com",
                "+7 999 999 99 99",
                LocalDate.of(1990, 5, 15)
        );
    }


    @Test
    void shouldSaveClient_WhenDataIsValid() {

        when(clientRepository.existsByEmail(clientRequestDto.email()))
                .thenReturn(false);
        when(clientMapper.clientRequestDtoToClient(clientRequestDto))
                .thenReturn(client);
        when(clientRepository.save(client))
                .thenReturn(client);
        when(clientMapper.clientToDto(client))
                .thenReturn(clientResponseDto);

        ClientResponseDto result = clientService.createClient(clientRequestDto);

        assertThat(result).isEqualTo(clientResponseDto);
        verify(clientRepository).save(client);
    }

    @Test
    void shouldThrowEmailAlreadyExistsException_WhenEmailAlreadyExists() {

        String email = clientRequestDto.email();

        when(clientRepository.existsByEmail(email))
                .thenReturn(true);

        assertThatThrownBy(() -> clientService.createClient(clientRequestDto))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage("Client with email '%s' already exists", email);
        verify(clientRepository, never()).save(any(Client.class));
    }
}
