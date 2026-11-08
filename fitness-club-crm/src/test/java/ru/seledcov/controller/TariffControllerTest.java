package ru.seledcov.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.seledcov.config.PostgresTestContainer;
import ru.seledcov.entity.Tariff;
import ru.seledcov.infrastructure.AccessTime;
import ru.seledcov.infrastructure.ClientCategory;
import ru.seledcov.infrastructure.SubscriptionType;
import ru.seledcov.repository.TariffRepository;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TariffControllerTest extends PostgresTestContainer {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TariffRepository tariffRepository;

    @Test
    @WithMockUser
    void shouldReturnCreated_WhenCreateTariffRequestIsValid() throws Exception {
        mockMvc.perform(
                        post("/app/v1/tariffs")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                          {
                                          "name": "Тренажерный зал",
                                          "type": "selfService",
                                          "clientCategory": "adult",
                                          "unlimited": true,
                                          "durationDays": 30,
                                          "accessTime": "allDay",
                                          "price": 3200.00
                                          }
                                        """)
                )

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Тренажерный зал"))
                .andExpect(jsonPath("$.type").value("SELF_SERVICE"))
                .andExpect(jsonPath("$.clientCategory").value("ADULT"))
                .andExpect(jsonPath("$.unlimited").value(true))
                .andExpect(jsonPath("$.totalVisits").value(nullValue()))
                .andExpect(jsonPath("$.durationDays").value(30))
                .andExpect(jsonPath("$.accessTime").value("ALL_DAY"))
                .andExpect(jsonPath("$.price").value(3200.0))
                .andExpect(jsonPath("$.active").value(false));

        assertThat(tariffRepository.count()).isEqualTo(1L);
        Tariff savedTariff = tariffRepository.findAll().getFirst();
        assertThat(savedTariff.isActive()).isFalse();
    }

    @Test
    @WithMockUser
    void shouldReturnCorrectTariffResponseDto_WhenTariffGetById() throws Exception {

        Tariff tariff = new Tariff();
        tariff.setName("Тренажерный зал");
        tariff.setType(SubscriptionType.SELF_SERVICE);
        tariff.setClientCategory(ClientCategory.ADULT);
        tariff.setUnlimited(true);
        tariff.setTotalVisits(null);
        tariff.setDurationDays(30);
        tariff.setAccessTime(AccessTime.ALL_DAY);
        tariff.setPrice(new BigDecimal("3500.00"));
        tariff.setActive(false);
        tariff.setFingerprint("6d2139281a8f9d6c702d7eb46342886f37cfc8464614e7ee045610586e3f0547");

        Tariff savedTariff = tariffRepository.saveAndFlush(tariff);

        mockMvc.perform(
                        get("/app/v1/tariffs/" + savedTariff.getId())
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedTariff.getId()))
                .andExpect(jsonPath("$.name").value("Тренажерный зал"))
                .andExpect(jsonPath("$.type").value("SELF_SERVICE"))
                .andExpect(jsonPath("$.clientCategory").value("ADULT"))
                .andExpect(jsonPath("$.unlimited").value(true))
                .andExpect(jsonPath("$.totalVisits").value(nullValue()))
                .andExpect(jsonPath("$.durationDays").value(30))
                .andExpect(jsonPath("$.accessTime").value("ALL_DAY"))
                .andExpect(jsonPath("$.price").value(3500.0))
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    @WithMockUser
    void shouldReturnAllTariffs_WhenTariffsExist() throws Exception {
        Tariff tariff1 = new Tariff();
        tariff1.setName("Тренажерный зал");
        tariff1.setType(SubscriptionType.SELF_SERVICE);
        tariff1.setClientCategory(ClientCategory.ADULT);
        tariff1.setUnlimited(true);
        tariff1.setTotalVisits(null);
        tariff1.setDurationDays(30);
        tariff1.setAccessTime(AccessTime.ALL_DAY);
        tariff1.setPrice(new BigDecimal("3500.00"));
        tariff1.setActive(false);
        tariff1.setFingerprint("6d2139281a8f9d6c702d7eb46342886f37cfc8464614e7ee045610586e3f0547");

        Tariff tariff2 = new Tariff();
        tariff2.setName("Персональное занаятие 12");
        tariff2.setType(SubscriptionType.PERSONAL);
        tariff2.setClientCategory(ClientCategory.ADULT);
        tariff2.setUnlimited(false);
        tariff2.setTotalVisits(12);
        tariff2.setDurationDays(60);
        tariff2.setAccessTime(null);
        tariff2.setPrice(new BigDecimal("16000.00"));
        tariff2.setActive(false);
        tariff2.setFingerprint("6d2139281a8f9d6c702d7eb46342886f37cfc8464614e7ee045610586e3f0548");

        Tariff savedTariff1 = tariffRepository.saveAndFlush(tariff1);
        Tariff savedTariff2 = tariffRepository.saveAndFlush(tariff2);

        mockMvc.perform(
                        get("/app/v1/tariffs")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath(
                        "$[*].name",
                        containsInAnyOrder(savedTariff1.getName(), savedTariff2.getName())
                ));
    }

    @Test
    @WithMockUser
    void shouldReturnBadRequest_WhenCreateTariffRequestIsInvalid() throws Exception {

        mockMvc.perform(
                        post("/app/v1/tariffs")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                          {
                                          "name": " ",
                                          "type": "selfService",
                                          "clientCategory": "adult",
                                          "unlimited": true,
                                          "durationDays": 30,
                                          "accessTime": "allDay",
                                          "price": 3200.00
                                          }
                                        """)
                )
                .andExpect(status().isBadRequest());
        assertThat(tariffRepository.count()).isZero();
    }

    @Test
    @WithMockUser
    void shouldReturnNotFound_WhenTariffDoesNotExist() throws Exception {
        long tariffId = 9999L;

        mockMvc.perform(
                        get("/app/v1/tariffs/" + tariffId)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value(String.format("Tariff with id '%d' not found", tariffId)));

    }
}
