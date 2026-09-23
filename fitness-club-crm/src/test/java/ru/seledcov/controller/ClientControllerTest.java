package ru.seledcov.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.seledcov.config.PostgresTestContainer;
import ru.seledcov.dto.ClientResponseDto;
import ru.seledcov.entity.Client;
import ru.seledcov.repository.ClientRepository;

import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ClientControllerTest extends PostgresTestContainer {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ClientRepository clientRepository;

    @Test
    @WithMockUser
    void shouldReturnCreated_WhenCreateClientRequestIsValid() throws Exception {
        mockMvc.perform(
                        post("/api/v1/clients")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                        "firstName": "Ivan",
                                        "lastName": "Ivanov",
                                        "email": "example@mail.com",
                                        "phone": "+7 999 999 99 99",
                                        "birthDate": "2001-04-25"
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Ivan"))
                .andExpect(jsonPath("$.lastName").value("Ivanov"))
                .andExpect(jsonPath("$.email").value("example@mail.com"))
                .andExpect(jsonPath("$.phone").value("+7 999 999 99 99"))
                .andExpect(jsonPath("$.birthDate").value("2001-04-25"));

    }

    @Test
    @WithMockUser
    void shouldReturnConflict_WhenClientWithDuplicateEmailIsAdded() throws Exception {
        mockMvc.perform(
                        post("/api/v1/clients")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                        "firstName": "Ivan",
                                        "lastName": "Ivanov",
                                        "email": "example@mail.com",
                                        "phone": "+7 999 999 99 99",
                                        "birthDate": "2001-04-25"
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("example@mail.com"));

        mockMvc.perform(
                        post("/api/v1/clients")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                          {
                                          "firstName": "Petr",
                                          "lastName": "Petrov",
                                          "email": "example@mail.com",
                                          "phone": "+7 888 888 88 88",
                                          "birthDate": "1995-06-10"
                                        }
                                        """)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message")
                        .value("Client with email 'example@mail.com' already exists"));
    }

    @Test
    @WithMockUser
    void shouldReturnCorrectClientResponse_WhenClientGetById() throws Exception {
        MvcResult result = mockMvc.perform(
                        post("/api/v1/clients")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                        "firstName": "Petr",
                                        "lastName": "Petrov",
                                        "email": "example@mail.com",
                                        "phone": "+7 888 888 88 88",
                                        "birthDate": "1995-06-10"
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        ClientResponseDto clientResponseDto = objectMapper.readValue(json, ClientResponseDto.class);
        Long clientId = clientResponseDto.id();

        mockMvc.perform(
                        get("/api/v1/clients/" + clientId)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clientId))
                .andExpect(jsonPath("$.firstName").value(clientResponseDto.firstName()))
                .andExpect(jsonPath("$.lastName").value(clientResponseDto.lastName()))
                .andExpect(jsonPath("$.email").value(clientResponseDto.email()))
                .andExpect(jsonPath("$.phone").value(clientResponseDto.phone()))
                .andExpect(jsonPath("$.birthDate").value(clientResponseDto.birthDate().toString()));
    }

    @Test
    @WithMockUser
    void shouldReturnNotFound_WhenClientDoesNotExist() throws Exception {
        Long nonExistentClientId = 999L;

        mockMvc.perform(
                        get("/api/v1/clients/" + nonExistentClientId)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value(String.format("Client with id '%d' not found", nonExistentClientId)));

    }

    @Test
    @WithMockUser
    void shouldReturnUpdatedClient_WhenClientIsUpdated() throws Exception {

        Client client = new Client();
        client.setFirstName("Petr");
        client.setLastName("Petrov");
        client.setEmail("example@mail.com");
        client.setPhone("+7 999 111 22 33");
        client.setBirthDate(LocalDate.of(1990, 5, 15));

        client = clientRepository.save(client);
        long clientId = client.getId();

        mockMvc.perform(
                        put("/api/v1/clients/" + clientId)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "firstName": "Ivan",
                                          "lastName": "Petrov",
                                          "email": "exampleUpdate@mail.com",
                                          "phone": "+7 999 111 22 33",
                                          "birthDate": "1990-05-15"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clientId))
                .andExpect(jsonPath("$.firstName").value("Ivan"))
                .andExpect(jsonPath("$.lastName").value("Petrov"))
                .andExpect(jsonPath("$.email").value("exampleUpdate@mail.com"))
                .andExpect(jsonPath("$.phone").value("+7 999 111 22 33"))
                .andExpect(jsonPath("$.birthDate").value("1990-05-15"));

    }

    @Test
    @WithMockUser
    void shouldReturnNotFound_WhenUpdatingNonExistingClient() throws Exception {
        long clientId = 999L;

        mockMvc.perform(
                        put("/api/v1/clients/" + clientId)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                         "firstName": "Ivan",
                                         "lastName": "Petrov",
                                         "email": "exampleUpdate@mail.com",
                                         "phone": "+7 999 111 22 33",
                                         "birthDate": "1990-05-15"
                                        }
                                        """)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value(String.format("Client with id '%d' not found", clientId))
                );
    }

    @Test
    @WithMockUser
    void shouldReturnConflict_WhenNewEmailAlreadyExists() throws Exception {
        Client client1 = new Client();
        client1.setFirstName("Ivan");
        client1.setLastName("Ivanov");
        client1.setEmail("Ivan@mail.com");
        client1.setPhone("+7 999 999 99 99");
        client1.setBirthDate(LocalDate.of(2001, 4, 25));
        client1 = clientRepository.save(client1);
        long clientId = client1.getId();

        Client client2 = new Client();
        client2.setFirstName("Petr");
        client2.setLastName("Petrov");
        client2.setEmail("Petr@mail.com");
        client2.setPhone("+7 888 888 88 88");
        client2.setBirthDate(LocalDate.of(1995, 6, 10));
        client2 = clientRepository.save(client2);

        mockMvc.perform(
                put("/api/v1/clients/" + clientId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                 "firstName": "Ivan",
                                 "lastName": "Petrov",
                                 "email": "Petr@mail.com",
                                 "phone": "+7 999 111 22 33",
                                 "birthDate": "1990-05-15"
                                }
                                """)
        )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message")
                        .value(String.format("Client with email '%s' already exists", client2.getEmail()))
                );
    }

    @Test
    @WithMockUser
    void shouldReturnNotFound_WhenDeleteClientNotExists() throws Exception {
        long clientId = 999L;
        mockMvc.perform(
                        delete("/api/v1/clients/" + clientId)
                                .with(csrf())
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value(String.format("Client with id '%d' not found", clientId))
                );
    }

    @Test
    @WithMockUser
    void shouldReturnNoContent_WhenClientIsDeleted() throws Exception {
        Client client = new Client();
        client.setFirstName("Petr");
        client.setLastName("Petrov");
        client.setEmail("example@mail.com");
        client.setPhone("+7 999 111 22 33");
        client.setBirthDate(LocalDate.of(1990, 5, 15));

        client = clientRepository.save(client);
        long clientId = client.getId();

        mockMvc.perform(
                        delete("/api/v1/clients/" + clientId)
                                .with(csrf())
                )
                .andExpect(status().isNoContent());
    }

}
