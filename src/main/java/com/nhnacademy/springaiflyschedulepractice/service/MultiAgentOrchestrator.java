package com.nhnacademy.springaiflyschedulepractice.service;

import com.nhnacademy.springaiflyschedulepractice.agent.FlightSearchAgent;
import com.nhnacademy.springaiflyschedulepractice.agent.PriceFilterAgent;
import com.nhnacademy.springaiflyschedulepractice.agent.TimerFilterAgent;
import com.nhnacademy.springaiflyschedulepractice.dto.FlightInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 기본 검색 - FlightSearchAgent
 * 가격 조건 검색 - FlightSearchAgent + PriceFilterAgent
 * 시간 조건 검색 - FlightSearchAgent + TimerFilterAgent
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MultiAgentOrchestrator {
    private final FlightSearchAgent flightSearchAgent;
    private final PriceFilterAgent priceFilterAgent;
    private final TimerFilterAgent timerFilterAgent;


    public Map<String, List<FlightInfoResponse>> basicSearch(
            String departure,
            String arrival,
            String date){

        log.info("MultiAgentOrchestrator - 기본 검색 시작");
        long startMs = System.currentTimeMillis();

        Map<String, List<FlightInfoResponse>> result = flightSearchAgent.searchAndGroupByAirline(departure, arrival, date);

        log.info("MultiAgentOrchestrator - 기본검색 종료 ({}ms)", System.currentTimeMillis() - startMs);
        return result;
    }

    public Map<String, List<FlightInfoResponse>> priceFilterSearch(
            String departure,
            String arrival,
            String date,
            Integer minPrice,
            Integer maxPrice){

        log.info("MultiAgentOrchestrator - 가격 조건 검새 시작. {} ~ {} 조회", minPrice, maxPrice);
        long startMs = System.currentTimeMillis();

        Map<String, List<FlightInfoResponse>> flights = flightSearchAgent.searchAndGroupByAirline(departure, arrival, date);
        Map<String, List<FlightInfoResponse>> result = new HashMap<>();
        for (Map.Entry<String, List<FlightInfoResponse>> entry : flights.entrySet()) {
            List<FlightInfoResponse> filtered = priceFilterAgent.filterByPriceRange(
                    entry.getValue(),
                    minPrice,
                    maxPrice);

            if (!filtered.isEmpty()) {
                result.put(entry.getKey(), filtered);
            }
        }

        log.info("MultiAgentOrchestrator - 가격 조건 검색 종료 ({}ms)", System.currentTimeMillis() - startMs);
        return result;

    }

    public Map<String, List<FlightInfoResponse>> timeFilterSearch(
            String departure,
            String arrival,
            String date,
            String afterTime){
        log.info("MultiAgentOrchestrator - 시간 조건 검색 시작. {} 이후 조회", afterTime);
        long startMs = System.currentTimeMillis();

        Map<String, List<FlightInfoResponse>> flights = flightSearchAgent.searchAndGroupByAirline(departure, arrival, date);
        LocalTime parsedAfterTime = timerFilterAgent.parseTime(afterTime);

        Map<String, List<FlightInfoResponse>> result = new HashMap<>();
        for (Map.Entry<String, List<FlightInfoResponse>> entry : flights.entrySet()) {
            List<FlightInfoResponse> filtered = timerFilterAgent.filterAfterTime(
                    entry.getValue(),
                    parsedAfterTime);

            if (!filtered.isEmpty()) {
                result.put(entry.getKey(), filtered);
            }
        }
        log.info("MultiAgentOrchestrator - 시간 조건 검색 종료 ({}ms)", System.currentTimeMillis() - startMs);

        return result;

    }

}
