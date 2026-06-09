package com.nhnacademy.springaiflyschedulepractice.logging;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ChatLoggingAdvisor implements CallAdvisor {
    private final ChatLoggingCallback callback;

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        long startTime = callback.beforeRequest(request);

        try {
            ChatClientResponse response = chain.nextCall(request); //ChatModelCallAdvisor 호출 -> LLM실행

            callback.afterResponse(response, startTime);
            return response;
        } catch (RuntimeException e) {
            callback.onError(request, startTime, e);
            throw e;
        }
    }

    @Override
    public String getName() {
        return "request-response-logging-advisor";
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 100;
    }
}
