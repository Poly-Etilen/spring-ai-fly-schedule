package com.nhnacademy.springaiflyschedulepractice.service;

import com.nhnacademy.springaiflyschedulepractice.agent.*;
import com.nhnacademy.springaiflyschedulepractice.dto.FlightInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 자연어 항공편 검색 오케스트레이션 서비스
 *
 * LLM 파라미터 추출(빠진값이 있으면 보완) + A2A 에이전트 조율
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NLOrchestrationService {

    private final LlmAnalysisService llmAnalysisService;
    private final DateParserAgent dateParserAgent;
    private final AirportCodeAgent airportCodeAgent;
    private final FlightSearchAgent flightSearchAgent;
    private final TimeFilterAgent timeFilterAgent;
    private final PriceFilterAgent priceFilterAgent;
    private final GroupingAgent groupingAgent;


    public OrchestrationResult orchestrateFlightSearch(String message) {
        log.info("자연어 항공편 검색 오케스트레이션 시작");
        log.info("메시지: {}", message);

        log.info("단계 1: LLM 파라미터 추출");
        Map<String, Object> params = llmAnalysisService.extractFlightSearchParam(message);
        params = normalizeParams(message, params);

        log.info("단계 2: 파라미터 검증");
        if (!hasText(params.get("departure")) || !hasText(params.get("arrival"))) {
            return OrchestrationResult.error("출발 공항과 도착 공항을 명확히 입력해주세요.");
        }

        log.info("단계 3: 날짜 처리");
        String dateStr = (String) params.getOrDefault("date", "내일");
        String parsedDate = dateParserAgent.parseDate(dateStr);
        log.info("날짜: {} → {}", dateStr, parsedDate);

        log.info("단계 4: 공항 코드 변환");
        String departure = (String) params.get("departure");
        String arrival = (String) params.get("arrival");
        String depCode = airportCodeAgent.getAirportCode(departure);
        String arrCode = airportCodeAgent.getAirportCode(arrival);
        log.info("공항: {} → {}, {} → {}", departure, depCode, arrival, arrCode);

        log.info("단계 5: 항공편 검색");
        Map<String, List<FlightInfoResponse>> flightsMap =
                flightSearchAgent.searchAndGroupByAirline(depCode, arrCode, parsedDate);
        List<FlightInfoResponse> flights = flightsMap.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
        log.info("검색된 항공편: {}편", flights.size());

        if (params.containsKey("afterTime")) {
            log.info("단계 6a: 시간 필터링 (이후)");
            String afterTime = (String) params.get("afterTime");
            LocalTime time = timeFilterAgent.parseTime(afterTime);
            flights = timeFilterAgent.filterAfterTime(flights, time);
            log.info("{} 이후 필터링: {}편", afterTime, flights.size());
        }

        if (params.containsKey("beforeTime")) {
            log.info("단계 6b: 시간 필터링 (이전)");
            String beforeTime = (String) params.get("beforeTime");
            LocalTime time = timeFilterAgent.parseTime(beforeTime);
            flights = timeFilterAgent.filterBeforeTime(flights, time);
            log.info("{} 이전 필터링: {}편", beforeTime, flights.size());
        }

        if (params.containsKey("minPrice") || params.containsKey("maxPrice")) {
            log.info("단계 7: 가격 필터링");
            Integer minPrice = params.containsKey("minPrice") ?
                    parsePriceParam(params.get("minPrice")) : null;
            Integer maxPrice = params.containsKey("maxPrice") ?
                    parsePriceParam(params.get("maxPrice")) : null;
            flights = priceFilterAgent.filterByPriceRange(flights, minPrice, maxPrice);
            log.info("가격 필터링: {}편", flights.size());
        }


        log.info("단계 8: 항공사별 그룹핑");
        Map<String, List<FlightInfoResponse>> groupedFlights = groupingAgent.groupByAirline(flights);
        log.info("그룹핑 완료");

        Map<String, List<Map<String, Object>>> resultData = convertToResultMap(groupedFlights);


        log.info("오케스트레이션 완료");


        return OrchestrationResult.success(params, resultData);
    }


    private Map<String, List<Map<String, Object>>> convertToResultMap(
            Map<String, List<FlightInfoResponse>> groupedFlights) {

        return groupedFlights.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .map(this::convertFlightToMap)
                                .collect(Collectors.toList())
                ));
    }

    private Map<String, Object> convertFlightToMap(FlightInfoResponse flight) {
        return Map.of(
                "vihicleId", flight.getFlightId(),
                "airlineNm", flight.getAirlineName(),
                "depTime", flight.getDepartureTime(),
                "arrTime", flight.getArrivalTime(),
                "economyCharge", flight.getEconomyCharge()
        );
    }

    private boolean hasText(Object value) {
        return value instanceof String text && !text.isBlank() && !"null".equalsIgnoreCase(text);
    }

    private Map<String, Object> normalizeParams(String message, Map<String, Object> params) {
        Map<String, Object> normalized = new HashMap<>();
        params.forEach((key, value) -> {
            if (value != null && !"null".equalsIgnoreCase(value.toString()) && !value.toString().isBlank()) {
                normalized.put(key, value);
            }
        });

        fillRoute(message, normalized);
        fillDate(message, normalized);
        fillTime(message, normalized);
        fillPrice(message, normalized);

        Object date = normalized.get("date");

        if (date instanceof String dateText && !isDateExpression(dateText)) {
            if (!normalized.containsKey("afterTime") && dateText.contains("이후")) {
                normalized.put("afterTime", dateText);
            } else if (!normalized.containsKey("beforeTime")
                    && (dateText.contains("이전") || dateText.contains("전") || dateText.contains("까지"))) {
                normalized.put("beforeTime", dateText);
            }
            normalized.put("date", "내일");
        }

        log.info("정규화된 파라미터: {}", normalized);
        return normalized;
    }

    private void fillRoute(String message, Map<String, Object> params) {
        if (hasText(params.get("departure")) && hasText(params.get("arrival"))) {
            return;
        }

        Matcher matcher = Pattern.compile("([가-힣]+)\\s*(?:-|에서)\\s*([가-힣]+)").matcher(message);
        if (matcher.find()) {
            params.putIfAbsent("departure", extractAirportName(matcher.group(1)));
            params.putIfAbsent("arrival", extractAirportName(matcher.group(2)));
        }
    }

    private String extractAirportName(String value) {
        for (String airport : List.of("광주", "제주", "김포", "인천", "김해", "부산", "대구", "청주")) {
            if (value.contains(airport)) {
                return airport;
            }
        }
        return value;
    }

    private void fillDate(String message, Map<String, Object> params) {
        if (hasText(params.get("date"))) {
            return;
        }

        if (message.contains("내일")) {
            params.put("date", "내일");
        } else if (message.contains("모레")) {
            params.put("date", "모레");
        }
    }

    private void fillTime(String message, Map<String, Object> params) {
        if (hasText(params.get("afterTime")) || hasText(params.get("beforeTime"))) {
            return;
        }

        Matcher matcher = Pattern.compile("((?:오전|오후)?\\s*\\d{1,2}(?::\\d{2})?\\s*시?)\\s*(이후|이전|전|까지)").matcher(message);
        if (matcher.find()) {
            if ("이후".equals(matcher.group(2))) {
                params.put("afterTime", matcher.group(1));
            } else {
                params.put("beforeTime", matcher.group(1));
            }
        }
    }

    private void fillPrice(String message, Map<String, Object> params) {
        Matcher rangeMatcher = Pattern.compile("(\\d+)\\s*만원\\s*(?:에서|부터|~|-)\\s*(\\d+)\\s*만원").matcher(message);
        if (rangeMatcher.find()) {
            params.putIfAbsent("minPrice", String.valueOf(Integer.parseInt(rangeMatcher.group(1)) * 10000));
            params.putIfAbsent("maxPrice", String.valueOf(Integer.parseInt(rangeMatcher.group(2)) * 10000));
            return;
        }

        Matcher maxMatcher = Pattern.compile("(\\d+)\\s*만원\\s*(?:이하|까지|아래)").matcher(message);
        if (maxMatcher.find()) {
            params.putIfAbsent("maxPrice", String.valueOf(Integer.parseInt(maxMatcher.group(1)) * 10000));
        }
    }

    private boolean isDateExpression(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }

        String normalized = value.trim();
        return normalized.equals("오늘")
                || normalized.equals("내일")
                || normalized.equals("모레")
                || normalized.equals("내일모레")
                || normalized.matches("\\d{8}")
                || normalized.matches("\\d{4}-\\d{2}-\\d{2}");
    }

    private Integer parsePriceParam(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number number) {
            return number.intValue();
        }

        String normalized = value.toString().replaceAll("[^0-9]", "");
        if (normalized.isBlank()) {
            return null;
        }

        int price = Integer.parseInt(normalized);
        return price < 1000 ? price * 10000 : price;
    }

}
