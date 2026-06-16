package com.nhnacademy.springaiflyschedulepractice.service;

import com.nhnacademy.springaiflyschedulepractice.dto.FlightDetail;
import com.nhnacademy.springaiflyschedulepractice.dto.FlightInfoResponse;
import com.nhnacademy.springaiflyschedulepractice.dto.FlightSearchParam;
import com.nhnacademy.springaiflyschedulepractice.dto.airline.AirlineGroup;
import com.nhnacademy.springaiflyschedulepractice.service.agent.FlightSearchAgent;
import com.nhnacademy.springaiflyschedulepractice.service.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlightCoordinator {
    private final DateParser dateParser;
    private final AirportCodeConverter airportCodeConverter;
    private final FlightSearchAgent flightSearchAgent;
    private final TimeFilter timeFilter;
    private final PriceFilter priceFilter;
    private final FlightGrouper flightGrouper;
    private final ParameterNormalizer parameterNormalizer;

    public List<AirlineGroup> executeSearchWorkflow(FlightSearchParam params) {
        log.info("항공권 도메인 코디네이터 작동");

        String dateStr = parameterNormalizer.hasText(params.date()) ? params.date() : "내일";
        String parsedDate = dateParser.parseDate(dateStr);
        String depCode = airportCodeConverter.getAirportCode(params.departure());
        String arrCode = airportCodeConverter.getAirportCode(params.arrival());

        List<FlightInfoResponse> flights = flightSearchAgent.searchFlights(depCode,arrCode, parsedDate);

        if (parameterNormalizer.hasText(params.afterTime())) {
            flights = timeFilter.filterAfterTime(flights, timeFilter.parseTime(params.afterTime()));
        }
        if (params.minPrice() != null && params.maxPrice() != null) {
            flights = priceFilter.filterByPriceRange(flights, params.minPrice(), params.maxPrice());
        }
        Map<String, List<FlightInfoResponse>> grouped = flightGrouper.groupByAirline(flights);

        return grouped.entrySet().stream()
                .map(entry -> new AirlineGroup(
                        entry.getKey(),
                        entry.getValue().stream()
                                .map(f -> new FlightDetail(
                                        f.getFlightId(),
                                        f.getAirlineName(),
                                        f.getDepartureTime(),
                                        f.getArrivalTime(),
                                        parameterNormalizer.parseInteger(f.getEconomyCharge()))
                                ).toList()
                )).toList();
    }
}
