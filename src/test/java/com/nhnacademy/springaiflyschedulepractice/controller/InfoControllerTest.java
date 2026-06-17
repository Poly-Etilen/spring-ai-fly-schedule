package com.nhnacademy.springaiflyschedulepractice.controller;

import com.nhnacademy.springaiflyschedulepractice.dto.airline.AirlineInfoResponse;
import com.nhnacademy.springaiflyschedulepractice.dto.airport.AirportInfoResponse;
import com.nhnacademy.springaiflyschedulepractice.mvc.controller.InfoController;
import com.nhnacademy.springaiflyschedulepractice.service.ApiClientService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InfoController.class)
class InfoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ApiClientService apiClientService;
    @MockitoBean
    private CacheManager cacheManager;

    @Test
    @DisplayName("공항 목록 조회 시 200 Ok와 JSON 배열 반환")
    void getAirports() throws Exception {
        given(apiClientService.getAirportList()).willReturn(List.of());
        mockMvc.perform(get("/api/no-llm/info/airports")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("항공사 목록 조회 시 200 Ok와 Json 배열 반환")
    void getAirlines() throws Exception {
        given(apiClientService.getAirlineList()).willReturn(List.of());

        mockMvc.perform(get("/api/no-llm/info/airlines")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("공항 이름으로 코드 조회 시 성공 및 실패 케이스")
    void getAirportByCode() throws Exception {
        given(apiClientService.getAirportList()).willReturn(List.of(
                new AirportInfoResponse("NAARKJJ", "광주")
        ));

        mockMvc.perform(get("/api/no-llm/info/airports/code")
                .param("name", "광주"))
                .andExpect(status().isOk())
                .andExpect(content().string("NAARKJJ"));

        mockMvc.perform(get("/api/no-llm/info/airports/code")
                .param("name", "제주"))
                .andExpect(status().isOk())
                .andExpect(content().string("알 수 없는 공항입니다."));
    }

    @Test
    @DisplayName("항공사 이름으로 코드 조회 시 성공 및 실패 케이스")
    void getAirlineByCode() throws Exception {
        given(apiClientService.getAirlineList()).willReturn(List.of(
                new AirlineInfoResponse("AAR", "아시아나항공")
        ));

        mockMvc.perform(get("/api/no-llm/info/airlines/code")
                .param("name", "아시아나항공"))
                .andExpect(status().isOk())
                .andExpect(content().string("AAR"));

        mockMvc.perform(get("/api/no-llm/info/airlines/code")
                .param("name", "대한항공"))
                .andExpect(status().isOk())
                .andExpect(content().string("알 수 없는 항공사입니다."));
    }
}
