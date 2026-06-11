package com.nhnacademy.springaiflyschedulepractice.dto;

import com.nhnacademy.springaiflyschedulepractice.config.DataGoKrApiProperties;

import java.util.List;
import java.util.Map;

public record OrchestrationResult(
        boolean success,
        String message,
        FlightSearchParam param,
        Map<String, List<FlightDetail>> data
) {
    public static OrchestrationResult success(FlightSearchParam param, Map<String, List<FlightDetail>> data) {
        return new OrchestrationResult(true, "성공", param, data);
    }
}
