package com.nhnacademy.springaiflyschedulepractice.dto;

import com.nhnacademy.springaiflyschedulepractice.dto.airline.AirlineGroup;

import java.util.List;

public record AiFlightSearchResult(
        boolean success,
        String message,
        FlightSearchParam param,
        List<AirlineGroup> data
) {
    public static AiFlightSearchResult success(FlightSearchParam param, List<AirlineGroup> data) {
        return new AiFlightSearchResult(true, "성공", param, data);
    }

    public static AiFlightSearchResult error(String message) {
        return new AiFlightSearchResult(false, message, null, null);
    }
}
