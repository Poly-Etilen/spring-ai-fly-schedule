package com.nhnacademy.springaiflyschedulepractice.logging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;

@RequiredArgsConstructor
@Slf4j
public class ToolLoggingCallback implements ToolCallback {
    private static final int MAX_LEN = 500;

    private final ToolCallback toolCallback;

    @Override
    public ToolDefinition getToolDefinition() {
        return toolCallback.getToolDefinition();
    }

    //toolInput = LLM이 만든 Tool 파라미터 ex) departure:"광주", arrival:"제주"
    //toolContext = 애플리케이션이 Tool 실행에 추가로 넣어주는 세부 정보
    @Override
    public String call(String toolInput) {
        return executeWithLogging(toolInput, null);
    }

    @Override
    public String call(String toolInput, @Nullable ToolContext toolContext) {
        return executeWithLogging(toolInput, toolContext);
    }

    private String executeWithLogging(String toolInput, ToolContext toolContext){
        String toolName = getToolDefinition().name();
        long startTimeMs = System.currentTimeMillis();

        log.info("[Tool 시작] name={}, input={}", toolName, limit(toolInput));

        try {
            String result = toolContext == null
                    ? toolCallback.call(toolInput)
                    : toolCallback.call(toolInput, toolContext);
            long endTimeMs = System.currentTimeMillis();
            long duration = endTimeMs - startTimeMs;

            log.info("[Tool 완료] name={}, duration={}ms, result={}",
                    toolName, duration, limit(result));

            return result;
        } catch (RuntimeException e) {
            long endTimeMs = System.currentTimeMillis();
            long duration = endTimeMs - startTimeMs;

            log.error("[Tool 실패] name={}, duration={}ms, input={}",
                    toolName, duration, limit(toolInput), e);

            throw e;
        }
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
