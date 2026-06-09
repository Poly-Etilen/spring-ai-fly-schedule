package com.nhnacademy.springaiflyschedulepractice.agent;

import com.nhnacademy.springaiflyschedulepractice.dto.FlightInfoResponse;
import com.nhnacademy.springaiflyschedulepractice.service.ApiClientService;
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
    private final GroupingAgent groupingAgent;

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
}
