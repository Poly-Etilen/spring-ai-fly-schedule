package com.nhnacademy.springaiflyschedulepractice.tool;

import com.nhnacademy.springaiflyschedulepractice.dto.FlightInfoResponse;
import com.nhnacademy.springaiflyschedulepractice.service.agent.FlightSearchAgent;
import com.nhnacademy.springaiflyschedulepractice.service.util.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FlightSearchToolTest {
    @Mock private DateParser dateParser;
    @Mock private AirportCodeConverter airportCodeConverter;
    @Mock private FlightSearchAgent flightSearchAgent;
    @Mock private TimeFilter timeFilter;
    @Mock private PriceFilter priceFilter;
    @Mock private FlightGrouper flightGrouper;

    @InjectMocks
    private FlightSearchTool flightSearchTool;

    @Test
    @DisplayName("모든 필터 조건이 주어졌을 때 검색 및 최대 3개 제한 로직 검증")
    void searchFlightsByAirlineTest() {
        String departure = "광주";
        String arrival = "제주";
        String date = "내일";
        String afterTime = "1400";
        String beforeTime = "1800";
        Integer minPrice = 30000;
        Integer maxPrice = 80000;

        given(dateParser.parseDate(date)).willReturn("20260608");
        given(airportCodeConverter.getAirportCode(departure)).willReturn("NAARKJJ");
        given(airportCodeConverter.getAirportCode(arrival)).willReturn("NAARKPC");

        List<FlightInfoResponse> mockFlights = List.of(new FlightInfoResponse());
        given(flightSearchAgent.searchFlights("NAARKJJ", "NAARKPC", "20260608")).willReturn(mockFlights);

        LocalTime mockAfterTime = LocalTime.of(14, 0);
        LocalTime mockBeforeTime = LocalTime.of(18, 0);
        given(timeFilter.parseTime(afterTime)).willReturn(mockAfterTime);
        given(timeFilter.parseTime(beforeTime)).willReturn(mockBeforeTime);
        given(timeFilter.filterAfterTime(anyList(), eq(mockAfterTime))).willReturn(mockFlights);
        given(timeFilter.filterBeforeTime(anyList(), eq(mockBeforeTime))).willReturn(mockFlights);
        given(priceFilter.filterByPriceRange(anyList(), eq(minPrice), eq(maxPrice))).willReturn(mockFlights);

        Map<String, List<FlightInfoResponse>> groupedFlights = new HashMap<>();
        groupedFlights.put("대한항공", List.of(
                new FlightInfoResponse(), new FlightInfoResponse(), new FlightInfoResponse(), new FlightInfoResponse()
        ));
        groupedFlights.put("아시아나항공", List.of(new FlightInfoResponse()));
        given(flightGrouper.groupByAirline(anyList())).willReturn(groupedFlights);

        Map<String, List<FlightInfoResponse>> result = flightSearchTool.searchFlightsByAirline(
                departure, arrival, date, afterTime, beforeTime, minPrice, maxPrice
        );

        verify(flightSearchAgent).searchFlights("NAARKJJ", "NAARKPC", "20260608");
        verify(timeFilter).filterAfterTime(anyList(), eq(mockAfterTime));
        verify(timeFilter).filterBeforeTime(anyList(), eq(mockBeforeTime));
        verify(priceFilter).filterByPriceRange(anyList(), eq(minPrice), eq(maxPrice));

        assertTrue(result.containsKey("대한항공"));
        assertEquals(3, result.get("대한항공").size());

        assertTrue(result.containsKey("아시아나항공"));
        assertEquals(1, result.get("아시아나항공").size());
    }
}
