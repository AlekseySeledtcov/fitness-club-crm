package ru.seledcov.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.seledcov.dto.ClientRequestDto;
import ru.seledcov.dto.ClientResponseDto;
import ru.seledcov.entity.Client;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    ClientResponseDto clientToDto(Client client);

    List<ClientResponseDto> clientsToDto(List<Client> clients);

    @Mapping(target = "id", ignore = true)
    Client clientRequestDtoToClient(ClientRequestDto clientRequestDto);

    @Mapping(target = "id", ignore = true)
    void updateClientFromClientRequestDto(
            ClientRequestDto dto,
            @MappingTarget Client client
    );
}
