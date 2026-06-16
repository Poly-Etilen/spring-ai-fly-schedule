package com.nhnacademy.springaiflyschedulepractice.service;

import com.nhnacademy.springaiflyschedulepractice.agent.*;
import com.nhnacademy.springaiflyschedulepractice.dto.AiFlightSearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


/**
 * 자연어 항공편 검색 오케스트레이션 서비스
 *
 * LLM 파라미터 추출(빠진값이 있으면 보완) + A2A 에이전트 조율
 */
@Slf4j
@Service
public class NLOrchestrationService {

    private final ChatClient ollamaChatClient;
    private final ChatClient geminiChatClient;

    public NLOrchestrationService(
            @Qualifier("ollamaChatClientBuilder") ChatClient.Builder ollamaBuilder,
            @Qualifier("geminiChatClientBuilder") ChatClient.Builder geminiBuilder) {
        this.ollamaChatClient = ollamaBuilder.build();
        this.geminiChatClient = geminiBuilder.build();
    }


    public AiFlightSearchResult orchestrateFlightSearch(String message, String model) {
        try {
            ChatClient activeClient = model.equalsIgnoreCase("gemini") ? geminiChatClient : ollamaChatClient;
            return activeClient.prompt()
                    .system("""
                            너는 항공편 검색을 총괄하는 전문 AI 오케스트레이터야.
                            제공된 항공 관련 도구(Tool)들을 자율적으로 판단하여 호출하고 사용자의 요청에 맞는 데이터를 수집해라.
                            최종 결과는 반드시 다른 부연 설명이나 마크다운 래퍼 없이, 
                            제공된 'AiFlightSearchResult' 클래스의 JSON 구조 규격에 완벽히 매핑되도록 객체 형태로 반환해야 한다.
                            """)
                    .user(message)
                    .call()
                    .entity(AiFlightSearchResult.class);
        } catch (Exception e) {
            log.error("AI 오케스트레이션 도중 치명적 오류 발생", e);
            return AiFlightSearchResult.error("오케스트레이션 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
