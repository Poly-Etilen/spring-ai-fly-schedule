package com.nhnacademy.springaiflyschedulepractice.service;

import com.nhnacademy.springaiflyschedulepractice.agent.*;
import com.nhnacademy.springaiflyschedulepractice.dto.FlightDetail;
import com.nhnacademy.springaiflyschedulepractice.dto.FlightInfoResponse;
import com.nhnacademy.springaiflyschedulepractice.dto.FlightSearchParam;
import com.nhnacademy.springaiflyschedulepractice.dto.OrchestrationResult;
import com.nhnacademy.springaiflyschedulepractice.dto.airline.AirlineGroup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

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
    private final ParameterNormalizerAgent normalizerAgent;
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
        FlightSearchParam params = llmAnalysisService.extractFlightSearchParam(message);
        params = normalizerAgent.normalize(message, params);

        log.info("단계 2: 파라미터 검증");
        if (!hasText(params.departure()) || !hasText(params.arrival())) {
            return OrchestrationResult.error("출발 공항과 도착 공항을 명확히 입력해주세요.");
        }

        log.info("단계 3: 날짜 처리");
        String dateStr = hasText(params.date()) ? params.date() : "내일";
        String parsedDate = dateParserAgent.parseDate(dateStr);
        log.info("날짜: {} → {}", dateStr, parsedDate);

        log.info("단계 4: 공항 코드 변환");
        String departure = params.departure();
        String arrival = params.arrival();
        String depCode = airportCodeAgent.getAirportCode(departure);
        String arrCode = airportCodeAgent.getAirportCode(arrival);
        log.info("공항: {} → {}, {} → {}", departure, depCode, arrival, arrCode);

        log.info("단계 5: 항공편 검색");
        Map<String, List<FlightInfoResponse>> flightsMap =
                flightSearchAgent.searchAndGroupByAirline(depCode, arrCode, parsedDate);
        List<FlightInfoResponse> flights = flightsMap.values().stream()
                .flatMap(List::stream)
                .toList();
        log.info("검색된 항공편: {}편", flights.size());

        if (hasText(params.afterTime())) {
            log.info("단계 6a: 시간 필터링 (이후)");
            String afterTime = params.afterTime();
            LocalTime time = timeFilterAgent.parseTime(afterTime);
            flights = timeFilterAgent.filterAfterTime(flights, time);
            log.info("{} 이후 필터링: {}편", afterTime, flights.size());
        }

        if (hasText(params.beforeTime())) {
            log.info("단계 6b: 시간 필터링 (이전)");
            String beforeTime = params.beforeTime();
            LocalTime time = timeFilterAgent.parseTime(beforeTime);
            flights = timeFilterAgent.filterBeforeTime(flights, time);
            log.info("{} 이전 필터링: {}편", beforeTime, flights.size());
        }

        if (params.minPrice() != null || params.maxPrice() != null) {
            log.info("단계 7: 가격 필터링");
            Integer minPrice = params.minPrice();
            Integer maxPrice = params.maxPrice();
            flights = priceFilterAgent.filterByPriceRange(flights, minPrice, maxPrice);
            log.info("가격 필터링: {}편", flights.size());
        }


        log.info("단계 8: 항공사별 그룹핑");
        Map<String, List<FlightInfoResponse>> groupedFlights = groupingAgent.groupByAirline(flights);
        log.info("그룹핑 완료");

        List<AirlineGroup> resultData = groupedFlights.entrySet().stream()
                        .map(entry -> new AirlineGroup(
                                entry.getKey(),
                                entry.getValue().stream()
                                        .map(this::convertFlightToDetail)
                                        .toList()
                        )).toList();


        log.info("오케스트레이션 완료");


        return OrchestrationResult.success(params, resultData);
    }

    private FlightDetail convertFlightToDetail(FlightInfoResponse flight) {
        return new FlightDetail(
                flight.getFlightId(),
                flight.getAirlineName(),
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                normalizerAgent.parseInteger(flight.getEconomyCharge())
        );
    }

    private boolean hasText(Object value) {
        return value instanceof String text && !text.isBlank() && !"null".equalsIgnoreCase(text);
    }

}
