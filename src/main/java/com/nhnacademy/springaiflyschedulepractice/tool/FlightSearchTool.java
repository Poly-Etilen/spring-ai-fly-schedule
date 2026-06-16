package com.nhnacademy.springaiflyschedulepractice.tool;

import com.nhnacademy.springaiflyschedulepractice.agent.*;
import com.nhnacademy.springaiflyschedulepractice.dto.FlightInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlightSearchTool implements AiTool{
    private final DateParserAgent dateParserAgent;
    private final AirportCodeAgent airportCodeAgent;
    private final FlightSearchAgent flightSearchAgent;
    private final TimeFilterAgent timeFilterAgent;
    private final PriceFilterAgent priceFilterAgent;
    private final GroupingAgent groupingAgent;

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
        String parsedDate = dateParserAgent.parseDate(date != null ? date : "내일");
        String depCode = airportCodeAgent.getAirportCode(departure);
        String arrCode = airportCodeAgent.getAirportCode(arrival);

        // 2. 항공편 기본 검색 (API 호출)
        List<FlightInfoResponse> flights = flightSearchAgent.searchFlights(depCode, arrCode, parsedDate);

        // 3. 시간 필터링 적용
        if (afterTime != null && !afterTime.isBlank()) {
            LocalTime time = timeFilterAgent.parseTime(afterTime);
            flights = timeFilterAgent.filterAfterTime(flights, time);
        }
        if (beforeTime != null && !beforeTime.isBlank()) {
            LocalTime time = timeFilterAgent.parseTime(beforeTime);
            flights = timeFilterAgent.filterBeforeTime(flights, time);
        }

        // 4. 가격 필터링 적용
        if (minPrice != null || maxPrice != null) {
            flights = priceFilterAgent.filterByPriceRange(flights, minPrice, maxPrice);
        }

        // 5. 항공사별 그룹핑
        Map<String, List<FlightInfoResponse>> groupedFlight = groupingAgent.groupByAirline(flights);

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

    private String getAirportCode(String airportName) {
        return switch (airportName) {
            case "김포" -> "NAARKSS";
            case "인천" -> "NAARKSI";
            case "김해" -> "NAARKPK";
            case "울산" -> "NAARKPU";
            case "무안" -> "NAARKJB";
            case "광주" -> "NAARKJJ";
            case "여수" -> "NAARKJY";
            case "제주" -> "NAARKPC";
            case "대구" -> "NAARKTN";
            case "포항" -> "NAARKTH";
            case "양양" -> "NAARKNY";
            case "청주" -> "NAARKTU";
            default -> "NAARKJJ";
        };
    }

    private String parseDate(String date) {
        if (date.equals("내일")) {
            return LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        } else if (date.equals("모레")) {
            return LocalDate.now().plusDays(2).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        } else if (date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return date.replace("-", "");
        } else {
            return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        }
    }

}
