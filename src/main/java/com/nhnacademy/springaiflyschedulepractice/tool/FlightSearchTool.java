package com.nhnacademy.springaiflyschedulepractice.tool;

import com.nhnacademy.springaiflyschedulepractice.dto.FlightInfoResponse;
import com.nhnacademy.springaiflyschedulepractice.service.agent.FlightSearchAgent;
import com.nhnacademy.springaiflyschedulepractice.service.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlightSearchTool implements AiTool{
    private final DateParser dateParser;
    private final AirportCodeConverter airportCodeConverter;
    private final FlightSearchAgent flightSearchAgent;
    private final TimeFilter timeFilter;
    private final PriceFilter priceFilter;
    private final FlightGrouper flightGrouper;

    @Tool(
            description = "항공편을 검색하고 조건(시간, 가격)에 맞게 필터링하여 항공사별로 반환합니다. " +
                    "출발 공항, 도착 공항, 날짜를 받아 항공편 목록을 제공합니다. " +
                    "빠른 응답을 위해 항공사별 최대 3편만 반환합니다."
    )
    public Map<String, List<FlightInfoResponse>> searchFlightsByAirline(
            @ToolParam(description = "출발 공항 이름 (예: 광주, 김포, 제주)") String departure,
            @ToolParam(description = "도착 공항 이름 (예: 제주, 광주, 김포)") String arrival,
            @ToolParam(description = "날짜 (예: 내일, 모레, 2026-06-08)") String date,
            @ToolParam(description = "이후 시간 필터링 (예: 1400, 없으면 null)", required = false) String afterTime,
            @ToolParam(description = "이전 시간 필터링 (예: 1800, 없으면 null)", required = false) String beforeTime,
            @ToolParam(description = "최소 가격 (예: 30000, 없으면 null)", required = false) Integer minPrice,
            @ToolParam(description = "최대 가격 (예: 80000, 없으면 null)", required = false) Integer maxPrice) {
        log.info("[AI Tool 호출됨] 파라미터 - 출발:{}, 도착:{}, 날짜:{}, 이후:{}, 이전:{}, 최소가:{}, 최대가:{}",
                departure, arrival, date, afterTime, beforeTime, minPrice, maxPrice);

        // 1. 공항 코드 및 날짜 파싱 (기존 private 메서드 대신 Agent 활용)
        String parsedDate = dateParser.parseDate(date != null ? date : "내일");
        String depCode = airportCodeConverter.getAirportCode(departure);
        String arrCode = airportCodeConverter.getAirportCode(arrival);

        // 2. 항공편 기본 검색 (API 호출)
        List<FlightInfoResponse> flights = flightSearchAgent.searchFlights(depCode, arrCode, parsedDate);

        // 3. 시간 필터링 적용
        if (afterTime != null && !afterTime.isBlank()) {
            LocalTime time = timeFilter.parseTime(afterTime);
            flights = timeFilter.filterAfterTime(flights, time);
        }
        if (beforeTime != null && !beforeTime.isBlank()) {
            LocalTime time = timeFilter.parseTime(beforeTime);
            flights = timeFilter.filterBeforeTime(flights, time);
        }

        // 4. 가격 필터링 적용
        if (minPrice != null || maxPrice != null) {
            flights = priceFilter.filterByPriceRange(flights, minPrice, maxPrice);
        }

        // 5. 항공사별 그룹핑
        Map<String, List<FlightInfoResponse>> groupedFlight = flightGrouper.groupByAirline(flights);

        // 6. 결과 제한 (항공사별 최대 3편)
        Map<String, List<FlightInfoResponse>> limitedFlight = new HashMap<>();
        groupedFlight.forEach((airline, airlineFlights) -> {
            if (airlineFlights.size() > 3) {
                limitedFlight.put(airline, airlineFlights.subList(0, 3));
            } else {
                limitedFlight.put(airline, airlineFlights);
            }
        });

        log.info("[AI Tool 처리 완료] 조건에 맞는 데이터 필터링 후 반환");
        return limitedFlight;
    }

}
