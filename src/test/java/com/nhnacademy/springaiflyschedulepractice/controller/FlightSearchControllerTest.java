package com.nhnacademy.springaiflyschedulepractice.controller;

import com.nhnacademy.springaiflyschedulepractice.dto.FlightSearchParam;
import com.nhnacademy.springaiflyschedulepractice.service.FlightCoordinator;
import com.nhnacademy.springaiflyschedulepractice.tool.FlightSearchTool;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FlightSearchController.class)
class FlightSearchControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FlightSearchTool flightSearchTool;
    @MockitoBean
    private CacheManager cacheManager;

    @Test
    @DisplayName("필수 파라미터만으로 검색 요청시 200 OK와 리스트 반환")
    void searchFlightWithRequiredParams() throws Exception {
        given(flightSearchTool.searchFlightsByAirline(any(), any(), any(), any(), any(), any(), any())).willReturn(Collections.emptyMap());
        mockMvc.perform(get("/api/flight/search")
                .param("departure","광주")
                .param("arrival", "제주")
                .param("date", "내일")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("항공편 검색 결과:")));
    }

    @Test
    @DisplayName("선택 파라미터를 포함하여 검색 요청시 200 Ok와 리스트 반환")
    void searchFlightWithOptionalParams() throws Exception {
        given(flightSearchTool.searchFlightsByAirline(any(), any(), any(), any(), any(), any(), any())).willReturn(Collections.emptyMap());

        mockMvc.perform(get("/api/flight/search")
                .param("departure", "광주")
                .param("arrival", "제주")
                .param("date", "26년 8월 6일")
                .param("afterTime", "1200")
                .param("maxPrice", "120000")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("항공편 검색 결과:")));
    }

    @Test
    @DisplayName("필수 파라미터 누락 시 400")
    void searchFlightMissingRequiredParams() throws Exception {
        mockMvc.perform(get("/api/flight/search")
                .param("arrival", "제주")
                .param("date", "20260707")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
