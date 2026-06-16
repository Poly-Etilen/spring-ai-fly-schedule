package com.nhnacademy.springaiflyschedulepractice.controller;

import com.nhnacademy.springaiflyschedulepractice.mvc.controller.InfoController;
import com.nhnacademy.springaiflyschedulepractice.service.ApiClientService;
import com.nhnacademy.springaiflyschedulepractice.tool.AirlineInfoTool;
import com.nhnacademy.springaiflyschedulepractice.tool.AirportInfoTool;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
}
