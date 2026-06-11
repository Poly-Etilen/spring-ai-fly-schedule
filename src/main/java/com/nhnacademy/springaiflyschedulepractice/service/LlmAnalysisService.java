package com.nhnacademy.springaiflyschedulepractice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.springaiflyschedulepractice.dto.FlightSearchParam;
import com.nhnacademy.springaiflyschedulepractice.logging.ChatLoggingAdvisor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * 자연어로 요청했을 경우 LLM을 통해 요청 메시지에서 파라미터 추출
 */
@Slf4j
@Service
public class LlmAnalysisService {
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LlmAnalysisService(
            @Qualifier("ollamaChatModel") ChatModel chatModel,
            ChatLoggingAdvisor chatLoggingAdvisor) {
        this.chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(chatLoggingAdvisor)
                .build();
    }

    public FlightSearchParam extractFlightSearchParam(String message) {
        log.info("LLM 파라미터 추출 시작: {}", message);

        String systemMessage = """
                너는 항공편 검색 파라미터 추출 전문가야.

                사용자 메시지에서 다음 파라미터를 추출해서 JSON 형식으로 반환해줘:
                - departure: 출발 공항 이름 (예: "광주", "김포", "제주")
                - arrival: 도착 공항 이름 (예: "제주", "김포", "부산")
                - date: 날짜 (예: "내일", "모레", "2026-03-10")
                - afterTime: "이후" 시간 조건 (예: "14:00", "오후 2시")
                - beforeTime: "이전" 시간 조건
                - minPrice: 최소 가격 (숫자만)
                - maxPrice: 최대 가격 (숫자만)

                파라미터가 없으면 null로 설정해줘.
                "광주-제주", "광주에서 제주", "광주에서 제주로" 같은 표현은 departure="광주", arrival="제주"로 추출해줘.
                "6만원 이하"는 maxPrice="60000"으로 추출해줘.
                "5만원에서 7만원 사이"는 minPrice="50000", maxPrice="70000"으로 추출해줘.
                반드시 유효한 JSON만 반환해줘.
                """;

        String userPrompt = String.format("""
                다음 메시지에서 파라미터를 추출해서 JSON으로 반환해줘:
                "%s"
                """, message);

        try {
            String response = chatClient.prompt()
                    .system(systemMessage)
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
