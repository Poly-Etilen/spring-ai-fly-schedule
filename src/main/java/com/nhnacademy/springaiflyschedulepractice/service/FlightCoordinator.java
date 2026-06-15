package com.nhnacademy.springaiflyschedulepractice.service;

import com.nhnacademy.springaiflyschedulepractice.agent.*;
import com.nhnacademy.springaiflyschedulepractice.dto.FlightDetail;
import com.nhnacademy.springaiflyschedulepractice.dto.FlightInfoResponse;
import com.nhnacademy.springaiflyschedulepractice.dto.FlightSearchParam;
import com.nhnacademy.springaiflyschedulepractice.dto.airline.AirlineGroup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlightCoordinator {
    private final DateParserAgent dateParserAgent;
    private final AirportCodeAgent airportCodeAgent;
    private final FlightSearchAgent flightSearchAgent;
    private final TimeFilterAgent timeFilterAgent;
    private final PriceFilterAgent priceFilterAgent;
    private final GroupingAgent groupingAgent;
    private final ParameterNormalizerAgent parameterNormalizerAgent;

    public List<AirlineGroup> executeSearchWorkflow(FlightSearchParam params) {
        log.info("항공권 도메인 코디네이터 작동");

        String dateStr = parameterNormalizerAgent.hasText(params.date()) ? params.date() : "내일";
        String parsedDate = dateParserAgent.parseDate(dateStr);
        String depCode = airportCodeAgent.getAirportCode(params.departure());
        String arrCode = airportCodeAgent.getAirportCode(params.arrival());

        List<FlightInfoResponse> flights = flightSearchAgent.searchFlights(depCode,arrCode, parsedDate);

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
