package com.nhnacademy.springaiflyschedulepractice.agent;

import com.nhnacademy.springaiflyschedulepractice.dto.FlightDetail;
import com.nhnacademy.springaiflyschedulepractice.dto.FlightInfoResponse;
import com.nhnacademy.springaiflyschedulepractice.dto.FlightSearchParam;
import com.nhnacademy.springaiflyschedulepractice.dto.airline.AirlineGroup;
import com.nhnacademy.springaiflyschedulepractice.service.ApiClientService;
import com.nhnacademy.springaiflyschedulepractice.service.ParameterNormalizerAgent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlightSearchAgent {
    private final ApiClientService apiClientService;
    private final DateParserAgent dateParserAgent;
    private final AirportCodeAgent airportCodeAgent;
    private final TimeFilterAgent timeFilterAgent;
    private final PriceFilterAgent priceFilterAgent;
    private final GroupingAgent groupingAgent;
    private final ParameterNormalizerAgent parameterNormalizerAgent;

    public Map<String, List<FlightInfoResponse>> searchAndGroupByAirline(String departure, String arrival, String date) {
        log.info("FlightSearchAgent: 항공편 검색 시작");

        // 단계 1: 날짜 파싱 (DateParserAgent)
        log.info("  단계 1: 날짜 파싱");
        String formattedDate = dateParserAgent.parseDate(date);
        log.info("  → 날짜: {} → {}", date, formattedDate);

        // 단계 2: 공항 코드 변환 (AirportCodeAgent)
        log.info("  단계 2: 공항 코드 변환");
        String depCode = airportCodeAgent.getAirportCode(departure);
        String arrCode = airportCodeAgent.getAirportCode(arrival);
        log.info("  → 출발: {} → {}", departure, depCode);
        log.info("  → 도착: {} → {}", arrival, arrCode);

        // 단계 3: API 호출
        log.info("  단계 3: 항공편 API 호출");
        List<FlightInfoResponse> flights = apiClientService.getFlightSchedule(
                depCode, arrCode, formattedDate);
        log.info("  → {}편 조회 완료", flights.size());

        // 단계 4: 항공사별 그룹핑 (GroupingAgent)
        log.info("  단계 4: 항공사별 그룹핑");
        Map<String, List<FlightInfoResponse>> grouped = groupingAgent.groupByAirline(flights);

        log.info("FlightSearchAgent: 항공편 검색 완료 ({}개 항공사)", grouped.size());
        return grouped;
    }

    public List<AirlineGroup> processFullSearchPipeline(FlightSearchParam params) {
        String dateStr = parameterNormalizerAgent.hasText(params.date()) ? params.date() : "내일";
        String parsedDate = dateParserAgent.parseDate(dateStr);
        String depCode = airportCodeAgent.getAirportCode(params.departure());
        String arrCode = airportCodeAgent.getAirportCode(params.arrival());

        List<FlightInfoResponse> flights = apiClientService.getFlightSchedule(depCode,arrCode, parsedDate);

        if (parameterNormalizerAgent.hasText(params.afterTime())) {
            flights = timeFilterAgent.filterAfterTime(flights, timeFilterAgent.parseTime(params.afterTime()));
        }
        if (params.minPrice() != null && params.maxPrice() != null) {
            flights = priceFilterAgent.filterByPriceRange(flights, params.minPrice(), params.maxPrice());
        }
        Map<String, List<FlightInfoResponse>> grouped = groupingAgent.groupByAirline(flights);

        return grouped.entrySet().stream()
                .map(entry -> new AirlineGroup(
                        entry.getKey(),
                        entry.getValue().stream()
                                .map(f -> new FlightDetail(
                                        f.getFlightId(),
                                        f.getAirlineName(),
                                        f.getDepartureTime(),
                                        f.getArrivalTime(),
                                        parameterNormalizerAgent.parseInteger(f.getEconomyCharge()))
                                ).toList()
                )).toList();
    }
}
