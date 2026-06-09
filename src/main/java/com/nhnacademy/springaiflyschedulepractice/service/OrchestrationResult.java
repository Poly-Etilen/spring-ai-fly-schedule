package com.nhnacademy.springaiflyschedulepractice.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public class OrchestrationResult {
    private final boolean success;
    private final String message;
    private final Map<String, Object> extractedParams;
    private final Map<String, List<Map<String, Object>>> data;


    public static OrchestrationResult success(Map<String, Object> params, Map<String, List<Map<String, Object>>> data){
        return new OrchestrationResult(true, "검색 완료", params, data);
    }

    public static OrchestrationResult error(String message){
        return new OrchestrationResult(false, message, null, Map.of());
    }

    public Map<String, Object> toResponse(){
        Map<String, Object> response = new HashMap<>();

        response.put("success", success);
        response.put("message", message);
        if(extractedParams != null){
            response.put("extractedParams", extractedParams);
        }

        if(data != null && !data.isEmpty()){
            response.put("data", data);
        }

        return response;
    }

}
