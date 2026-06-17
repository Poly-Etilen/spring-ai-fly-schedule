package com.nhnacademy.springaiflyschedulepractice.controller;

import com.nhnacademy.springaiflyschedulepractice.service.SimpleChatService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatTestController.class)
class ChatTestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CacheManager cacheManager;

    @MockitoBean
    private SimpleChatService simpleChatService;

    @Test
    @DisplayName("Ollama 모델 질문 응답 테스트")
    void askOllamaTest() throws Exception {
        given(simpleChatService.askOllama(anyString())).willReturn("Ollama 응답 내용");
        MvcResult result = mockMvc.perform(get("/api/chat/ollama")
                .param("question", "안녕하세요"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(content.contains("[Ollama 응답"));
        assertTrue(content.contains("Ollama 응답 내용"));
    }

    @Test
    @DisplayName("Gemini 모델 질문 응답 테스트")
    void askGeminiTest() throws Exception {
        given(simpleChatService.askGemini(anyString())).willReturn("Gemini 응답 내용");
        MvcResult result = mockMvc.perform(get("/api/chat/gemini")
                .param("question", "안녕하세요"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(content.contains("[Gemini 응답"));
        assertTrue(content.contains("Gemini 응답 내용"));
    }
}
