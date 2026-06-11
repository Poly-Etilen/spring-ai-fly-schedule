package com.nhnacademy.springaiflyschedulepractice.dto;

import com.nhnacademy.springaiflyschedulepractice.dto.airline.AirlineGroup;

import java.util.List;
import java.util.Map;

public record OrchestrationResult(
        boolean success,
        String message,
        FlightSearchParam param,
        List<AirlineGroup> data
) {
    public static OrchestrationResult success(FlightSearchParam param, List<AirlineGroup> data) {
        return new OrchestrationResult(true, "성공", param, data);
    }

    public static OrchestrationResult error(String message) {
        return new OrchestrationResult(false, message, null, null);
    }
}
