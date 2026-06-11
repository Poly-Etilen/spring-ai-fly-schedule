package com.nhnacademy.springaiflyschedulepractice.service;

import com.nhnacademy.springaiflyschedulepractice.agent.FlightSearchAgent;
import com.nhnacademy.springaiflyschedulepractice.agent.PriceFilterAgent;
import com.nhnacademy.springaiflyschedulepractice.agent.TimeFilterAgent;
import com.nhnacademy.springaiflyschedulepractice.dto.FlightDetail;
import com.nhnacademy.springaiflyschedulepractice.dto.FlightInfoResponse;
import com.nhnacademy.springaiflyschedulepractice.dto.airline.AirlineGroup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
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
    private final ParameterNormalizerAgent normalizer;
    private final FlightSearchAgent flightSearchAgent;
    private final PriceFilterAgent priceFilterAgent;
    private final TimeFilterAgent timeFilterAgent;


    public List<AirlineGroup> basicSearch(
            String departure,
            String arrival,
            String date){

        log.info("MultiAgentOrchestrator - 기본 검색 시작");
        long startMs = System.currentTimeMillis();

        Map<String, List<FlightInfoResponse>> result = flightSearchAgent.searchAndGroupByAirline(departure, arrival, date);

        log.info("MultiAgentOrchestrator - 기본검색 종료 ({}ms)", System.currentTimeMillis() - startMs);
        return convertToAirlineGroups(result);
    }

    public List<AirlineGroup> priceFilterSearch(
            String departure,
            String arrival,
            String date,
            Integer minPrice,
            Integer maxPrice){

        log.info("MultiAgentOrchestrator - 가격 조건 검새 시작. {} ~ {} 조회", minPrice, maxPrice);
        long startMs = System.currentTimeMillis();

        Map<String, List<FlightInfoResponse>> flights = flightSearchAgent.searchAndGroupByAirline(departure, arrival, date);

        log.info("MultiAgentOrchestrator - 가격 조건 검색 종료 ({}ms)", System.currentTimeMillis() - startMs);
        return flights.entrySet().stream()
                .map(entry -> {
                    List<FlightInfoResponse> filtered = priceFilterAgent.filterByPriceRange(entry.getValue(), minPrice, maxPrice);
                    return new AirlineGroup(entry.getKey(), convertToDetailList(filtered));
                })
                .filter(group -> !group.flights().isEmpty())
                .toList();

    }

    public List<AirlineGroup> timeFilterSearch(
            String departure,
            String arrival,
            String date,
            String afterTime){
        log.info("MultiAgentOrchestrator - 시간 조건 검색 시작. {} 이후 조회", afterTime);
        long startMs = System.currentTimeMillis();

        Map<String, List<FlightInfoResponse>> flights = flightSearchAgent.searchAndGroupByAirline(departure, arrival, date);
        LocalTime parsedAfterTime = timeFilterAgent.parseTime(afterTime);

        log.info("MultiAgentOrchestrator - 시간 조건 검색 종료 ({}ms)", System.currentTimeMillis() - startMs);

        return flights.entrySet().stream()
                .map(entry -> {
                    List<FlightInfoResponse> filtered = timeFilterAgent.filterAfterTime(entry.getValue(), parsedAfterTime);
                    return new AirlineGroup(entry.getKey(), convertToDetailList(filtered));
                })
                .filter(group -> !group.flights().isEmpty())
                .toList();


    }

    private List<AirlineGroup> convertToAirlineGroups(Map<String, List<FlightInfoResponse>> raw) {
        return raw.entrySet().stream()
                .map(entry -> new AirlineGroup(entry.getKey(), convertToDetailList(entry.getValue())))
                .toList();
    }

    private List<FlightDetail> convertToDetailList(List<FlightInfoResponse> rawList) {
        return rawList.stream()
                .map(f -> new FlightDetail(
                        f.getFlightId(),
                        f.getAirlineName(),
                        f.getDepartureTime(),
                        f.getArrivalTime(),
                        normalizer.parseInteger(f.getEconomyCharge())
                )).toList();
    }

}
