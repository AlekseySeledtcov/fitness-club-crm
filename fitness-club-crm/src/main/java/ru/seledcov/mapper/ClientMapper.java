package ru.seledcov.mapper;

import org.mapstruct.Mapper;
import ru.seledcov.dto.ClientRequestDto;
import ru.seledcov.dto.ClientResponseDto;
import ru.seledcov.entity.Client;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    ClientResponseDto clientToDto (Client client);

    List<ClientResponseDto> clientsToDto (List<Client> clients);

    Client clientRequestDtoToClient(ClientRequestDto clientRequestDto);
}
