package com.nhnacademy.springaiflyschedulepractice.service;

import com.nhnacademy.springaiflyschedulepractice.dto.FlightSearchParam;
import com.nhnacademy.springaiflyschedulepractice.logging.ChatLoggingAdvisor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.DefaultUsage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LlmAnalysisServiceTest {

    @Mock
    private ChatModel ollamaChatModel;

    @Mock
    private ChatModel geminiChatModel;

    private LlmAnalysisService llmAnalysisService;

    @BeforeEach
    void setUp() {
        llmAnalysisService = new LlmAnalysisService(
                ollamaChatModel,
                geminiChatModel,
                new ChatLoggingAdvisor()
        );

        ReflectionTestUtils.setField(
                llmAnalysisService,
                "systemPrompt",
                new ByteArrayResource("test system prompt".getBytes(StandardCharsets.UTF_8))
        );
    }


    @Test
    @DisplayName("llm이 json형식으로 정상 응답을 했을경우")
    void llmResponseSuccessTest(){
        String message = "테스트 요청 메세지";
        String model = "ollama";
        when(ollamaChatModel.call(any(Prompt.class))).thenReturn(chatResponse(
                """
                        다음은 테스트 요청메세지에대한 응답입니다.
                        {
                            "departure": "테스트 출발지",
                            "arrival": "테스트 도착지",
                            "date": "테스트 출발날짜",
                            "afterTime": "테스트 이후 시간",
                            "beforeTime": "테스트 이전 시간",
                            "minPrice": 0,
                            "maxPrice": 0
                        }
                        """
        ));


        FlightSearchParam expected = new FlightSearchParam("테스트 출발지", "테스트 도착지", "테스트 출발날짜","테스트 이후 시간","테스트 이전 시간", 0, 0);
        FlightSearchParam actual = llmAnalysisService.extractFlightSearchParam(message, model);
        assertEquals(expected, actual);
    }




    @Test
    @DisplayName("llm이 엉뚱한 응답을했을 경우")
    void llmResponseFailTest(){
        String message = "테스트 요청 메세지";
        String model = "ollama";
        when(ollamaChatModel.call(any(Prompt.class))).thenReturn(chatResponse(
                """
                        json이 포함되지않은 엉뚱한 응답.
                        """
        ));
        FlightSearchParam expected = new FlightSearchParam(null,null,null,null,null,null,null);
        FlightSearchParam actual = llmAnalysisService.extractFlightSearchParam(message, model);
        assertEquals(expected, actual);
    }

    private ChatResponse chatResponse(String content) {
        return new ChatResponse(
                List.of(new Generation(new AssistantMessage(content))),
                ChatResponseMetadata.builder()
                        .usage(new DefaultUsage(0, 0, 0))
                        .build()
        );
    }
}
