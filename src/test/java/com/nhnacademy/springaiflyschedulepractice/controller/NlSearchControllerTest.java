package com.nhnacademy.springaiflyschedulepractice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.springaiflyschedulepractice.dto.AiFlightSearchResult;
import com.nhnacademy.springaiflyschedulepractice.dto.NlSearchRequest;
import com.nhnacademy.springaiflyschedulepractice.service.CoordinatorSearchService;
import com.nhnacademy.springaiflyschedulepractice.service.NLOrchestrationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = NlSearchController.class)
@Import(FlightValidator.class)
class NlSearchControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CacheManager cacheManager;
    @MockitoBean
    private NLOrchestrationService nlOrchestrationService;
    @MockitoBean
    private CoordinatorSearchService coordinatorSearchService;

    @Test
    @DisplayName("Coordinator 타입 정상 요청 시 서비스 호출")
    void searchWithCoordinator() throws Exception {
        NlSearchRequest request = new NlSearchRequest();
        request.setMessage("내일 광주에서 제주도 가는 항공편 찾아줘");
        request.setModel("gemini");
        request.setType("coordinator");

        AiFlightSearchResult mockResult = AiFlightSearchResult.success(null, List.of());

        given(coordinatorSearchService.executeCoordinatorSearch(request.getMessage(), request.getModel())).willReturn(mockResult);

        mockMvc.perform(post("/api/nl-search/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("성공"));
    }

    @Test
    @DisplayName("Orchestrator 타입 정상 요청 시 서비스가 호출됨")
    void searchWithOrchestrator() throws Exception {
        NlSearchRequest request = new NlSearchRequest();
        request.setMessage("내일 광주에서 제주도 가는 항공편 찾아줘");
        request.setModel("gemini");
        request.setType("orchestrator");

        AiFlightSearchResult mockResult = AiFlightSearchResult.success(null, List.of());

        given(nlOrchestrationService.orchestrateFlightSearch(request.getMessage(), request.getModel())).willReturn(mockResult);

        mockMvc.perform(post("/api/nl-search/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Validation 실패 - 지원하지 않는 모델일 경우 에러 응답 반환")
    void searchWithInvalidModel() throws Exception {
        NlSearchRequest request = new NlSearchRequest();
        request.setMessage("내일 광주에서 제주도 가는 항공편 찾아줘");
        request.setModel("GPT");
        request.setType("orchestrator");

        mockMvc.perform(post("/api/nl-search/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("지원하지 않는 모델입니다."));
    }

    @Test
    @DisplayName("Validation 실패 - 메세지가 비어있을 경우 에러 응답 반환")
    void searchWithEmptyMessage() throws Exception {
        NlSearchRequest request = new NlSearchRequest();
        request.setMessage("");
        request.setModel("gemini");
        request.setType("orchestrator");

        mockMvc.perform(post("/api/nl-search/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("메세지를 입력해주세요"));
    }
}
