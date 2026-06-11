package com.nhnacademy.springaiflyschedulepractice.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class ChatLoggingAdvisor implements CallAdvisor {

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        log.info("[ChatClient 요청] prompt={}", request.prompt().getContents());
        long startTime = System.currentTimeMillis();

        try{
            ChatClientResponse response = chain.nextCall(request);

            log.info("[ChatClient 응답] duration={}ms, token={}, response={}",
                    System.currentTimeMillis() - startTime,
                    response.chatResponse().getMetadata().getUsage().getPromptTokens(),
                    response.chatResponse().getResult().getOutput().getText());

            return response;
        }catch (RuntimeException e){
            log.error("[ChatClient 에러] duration={}ms, prompt={}, error={}", System.currentTimeMillis() - startTime, request, e.getMessage());
            throw e;
        }
    }

    @Override
    public String getName() {
        return "ChatClient-logging-advisor";
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 100;
    }
}
