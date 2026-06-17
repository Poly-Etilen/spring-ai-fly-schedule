package com.nhnacademy.springaiflyschedulepractice.service;

import com.nhnacademy.springaiflyschedulepractice.dto.FlightDetail;
import com.nhnacademy.springaiflyschedulepractice.dto.FlightInfoResponse;
import com.nhnacademy.springaiflyschedulepractice.dto.FlightSearchParam;
import com.nhnacademy.springaiflyschedulepractice.dto.airline.AirlineGroup;
import com.nhnacademy.springaiflyschedulepractice.service.agent.FlightSearchAgent;
import com.nhnacademy.springaiflyschedulepractice.service.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class FlightCoordinatorTest {
    private DateParser dateParser;
    private AirportCodeConverter airportCodeConverter;
    private TimeFilter timeFilter;
    private PriceFilter priceFilter;
    private FlightGrouper flightGrouper;
    private ParameterNormalizer parameterNormalizer;

    private FlightSearchAgent flightSearchAgent;

    private FlightCoordinator flightCoordinator;

    @BeforeEach
    void setUp(){
        dateParser = new DateParser();
        airportCodeConverter = new AirportCodeConverter();
        timeFilter = new TimeFilter();
        priceFilter = new PriceFilter();
        flightGrouper = new FlightGrouper();
        parameterNormalizer = new ParameterNormalizer();

        flightSearchAgent = mock(FlightSearchAgent.class);

        flightCoordinator = new FlightCoordinator(
            dateParser,
            airportCodeConverter,
            flightSearchAgent,
            timeFilter,
            priceFilter,
            flightGrouper,
            parameterNormalizer
        );
    }


    @Test
    @DisplayName("메서드 호출 검사 및 반환값 검사")
    void executeSearchWorkflowTest(){
        FlightSearchParam testParam = new FlightSearchParam(
                "광주",
                "제주",
                "2026-06-17",
                null,
                null,
                null,
                null
        );


        List<FlightInfoResponse> expectedData = List.of(
               new FlightInfoResponse(
                       "테스트 도착 항공코드",
                       "테스트 항공편 아이디",
                       "테스트 항공사 이름",
                       "테스트 출발 시간",
                       "테스트 도착 시간",
                       "0",
                       "테스트 비즈니스석 가격",
                       "테스트 출발 항공코드"
               )
        );

        when(flightSearchAgent.searchFlights("NAARKJJ", "NAARKPC", "20260617"))
                .thenReturn(expectedData);

        List<AirlineGroup> actual = flightCoordinator.executeSearchWorkflow(testParam);

        AirlineGroup actualAirlineGroup = actual.getFirst();
        FlightDetail actualFlightDetail = actualAirlineGroup.flights().getFirst();
        assertAll(
                () -> assertEquals("테스트 항공사 이름", actualAirlineGroup.airlineName()),
                () -> assertEquals("테스트 항공편 아이디", actualFlightDetail.flightId()),
                () -> assertEquals("테스트 도착 시간", actualFlightDetail.arrivalTime()),
                () -> assertEquals("테스트 출발 시간", actualFlightDetail.departureTime()),
                () -> assertEquals(0, actualFlightDetail.price()),
                () -> assertEquals("테스트 항공사 이름", actualFlightDetail.airlineName()),

                () -> verify(flightSearchAgent, times(1)).searchFlights("NAARKJJ", "NAARKPC", "20260617")
        );
    }



}
