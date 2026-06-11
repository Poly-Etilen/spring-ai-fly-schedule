package com.nhnacademy.springaiflyschedulepractice.tool;

import com.nhnacademy.springaiflyschedulepractice.dto.FlightInfoResponse;
import com.nhnacademy.springaiflyschedulepractice.service.ApiClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlightSearchTool implements AiTool{
    private final ApiClientService apiClientService;

    @Tool(
            description = "항공편을 검색하여 항공사별로 그룸핑하여 반환합니다. " +
                    "출발 공항, 도착 공항, 날짜를 받아 항공사별로 정리된 항공편 목록을 제공합니다. " +
                    "날짜는 '내일', '모레', '2026-06-08' 형식을 지원합니다. " +
                    "빠른 응답을 위해 항공사별 최대3편만 반환합니다."
    )
    public Map<String, List<FlightInfoResponse>> searchFlightsByAirline(
            @ToolParam(description = "출발 공항 이름 (예: 광주, 김포, 제주)") String departure,
            @ToolParam(description = "도착 공항 이름 (예: 제주, 광주, 김포)") String arrival,
            @ToolParam(description = "날짜 (예: 내일, 모레, 2026-06-08)") String date) {
        String formattedDate = parseDate(date);
        String depAirportId = getAirportCode(departure);
        String arrAirportId = getAirportCode(arrival);
        List<FlightInfoResponse> allFlights = apiClientService.getFlightSchedule(depAirportId, arrAirportId, formattedDate);
        Map<String, List<FlightInfoResponse>> groupedFlight = allFlights.stream().collect(Collectors.groupingBy(FlightInfoResponse::getAirlineName));
        Map<String, List<FlightInfoResponse>> limitedFlight = new HashMap<>();

        groupedFlight.forEach((airline, flights) -> {
            if (flights.size() > 3) {
                limitedFlight.put(airline, flights.subList(0, 3));
            } else {
                limitedFlight.put(airline, flights);
            }
        });

//        log.info("Tool 응답: {}개 항공사, {}편", limitedFlight.size(), limitedFlight.values().stream().mapToInt(List::size).sum());
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
