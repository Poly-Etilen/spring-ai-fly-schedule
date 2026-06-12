package com.nhnacademy.springaiflyschedulepractice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.springaiflyschedulepractice.dto.FlightSearchParam;
import com.nhnacademy.springaiflyschedulepractice.logging.ChatLoggingAdvisor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 * 자연어로 요청했을 경우 LLM을 통해 요청 메시지에서 파라미터 추출
 */
@Slf4j
@Service
public class LlmAnalysisService {
    private final ChatClient ollamaClient;
    private final ChatClient geminiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("classpath:/prompts/flight-system.st")
    private Resource systemPrompt;

    public LlmAnalysisService(
            @Qualifier("ollamaChatModel") ChatModel ollamaModel,
            @Qualifier("googleGenAiChatModel") ChatModel geminiChatModel,
            ChatLoggingAdvisor chatLoggingAdvisor) {
        this.ollamaClient = ChatClient.builder(ollamaModel)
                .defaultAdvisors(chatLoggingAdvisor)
                .build();
        this.geminiClient = ChatClient.builder(geminiChatModel)
                .defaultAdvisors(chatLoggingAdvisor)
                .build();
    }

    public FlightSearchParam extractFlightSearchParam(String message, String model) {
        log.info("LLM 파라미터 추출 시작: {}", message);

        ChatClient activeClient = model.equalsIgnoreCase("gemini") ? geminiClient : ollamaClient;

        String userPrompt = String.format("""
                다음 메시지에서 파라미터를 추출해서 JSON으로 반환해줘:
                "%s"
                """, message);

        try {
            String response = activeClient.prompt()
                    .system(systemPrompt)
                    .user(userPrompt)
                    .call()
                    .content();

            log.info("LLM 응답: {}", response);

            return objectMapper.readValue(
                    extractJson(response),
                    FlightSearchParam.class
            );
        } catch (Exception e) {
            log.error("LLM 파라미터 추출 실패", e);
            return new FlightSearchParam(null,null,null,null,null,null,null);
        }
    }

    private String extractJson(String response) {
        if (response == null || response.isBlank()) {
            return "{}";
        }

        int start = response.indexOf('{');
        int end = response.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return response.substring(start, end + 1);
        }

        return response;
    }
}
