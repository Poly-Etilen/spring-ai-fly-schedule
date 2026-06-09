package com.nhnacademy.springaiflyschedulepractice.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 사용자 요청/응답 로깅 콜백
 * 1. 사용자 요청 로깅
 * 2. 최종 응답 로깅
 * 3. 전체 실행 시간 계산
 */
@Slf4j
@Component
public class ChatLoggingCallback {
    private static final int MAX_LEN = 500;//최대 글자수 500자

    public long beforeRequest(ChatClientRequest request){
        log.info("[ChatClient 요청] prompt={}", limit(request.prompt().getContents()));

        return System.currentTimeMillis();
    }

    public void afterResponse(ChatClientResponse response, long startTimeMs){
        long duration = System.currentTimeMillis() - startTimeMs;

        log.info("[ChatClient 응답] duration={}ms, response={}",
                duration, limit(Objects.requireNonNull(response.chatResponse()).getResult().getOutput().getText()));
    }


    public void onError(ChatClientRequest request, long startTimeMs, RuntimeException e){
        long duration = System.currentTimeMillis() - startTimeMs;

        log.error("[ChatClient 에러] duration={}ms, prompt={}", duration, request, e);
    }


    private String limit(String value){
        if(value == null){
            return "null";
        }

        if(value.length() <= MAX_LEN){
            return value;
        }

        return value.substring(0, MAX_LEN) + "...";
    }
}
