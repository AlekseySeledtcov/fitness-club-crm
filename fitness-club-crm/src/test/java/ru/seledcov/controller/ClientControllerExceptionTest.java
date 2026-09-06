package ru.seledcov.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.seledcov.dto.ClientRequestDto;
import ru.seledcov.handler.GlobalExceptionHandler;
import ru.seledcov.service.ClientService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import(GlobalExceptionHandler.class)
public class ClientControllerExceptionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClientService clientService;

    @Test
    @WithMockUser
    void shouldReturnConflict_WhenClientEmailConstraintIsViolated() throws Exception {

        Throwable rootCause = new RuntimeException(
                "duplicate key value violates unique constraint \"uk_client_email\""
        );
        DataIntegrityViolationException exception = new DataIntegrityViolationException(
                "Database error",
                rootCause);

        when(clientService.createClient(any(ClientRequestDto.class)))
                .thenThrow(exception);

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
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Client with email already exists"));
    }
}
