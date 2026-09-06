package ru.seledcov.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.seledcov.config.PostgresTestContainer;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ClientControllerTest extends PostgresTestContainer {

    @Autowired
    private MockMvc mockMvc;

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
}
