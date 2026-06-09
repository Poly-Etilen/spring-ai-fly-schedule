package com.nhnacademy.springaiflyschedulepractice.agent;

import com.nhnacademy.springaiflyschedulepractice.dto.FlightInfoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GroupingAgent {
    public Map<String, List<FlightInfoResponse>> groupByAirline(List<FlightInfoResponse> flights) {
        log.info("GroupingAgent: 항공사별 그룹핑 시작 ({}편)", flights.size());
        Map<String, List<FlightInfoResponse>> grouped = flights.stream()
                .collect(Collectors.groupingBy(FlightInfoResponse::getAirlineName));

        grouped.forEach((airline, airlineFlights) -> {
            log.info(" {}: {}편", airline, airlineFlights.size());
        });

        log.info("GroupingAgent: 그룹핑 완료 ({}개 항공사)", grouped.size());
        return grouped;
    }

    public Map<String, List<FlightInfoResponse>> groupByTimeSlot(List<FlightInfoResponse> flights) {
        log.info("GroupingAgent: 시간대별 그룹핑 시작");
        return flights.stream()
                .collect(Collectors.groupingBy(flight -> {
                    int hour = Integer.parseInt(flight.getDepartureTime().substring(0, 2));

                    if (hour < 12) return "오전";
                    else if (hour < 18) return "오후";
                    else return "저녁";
                }));
    }
}
