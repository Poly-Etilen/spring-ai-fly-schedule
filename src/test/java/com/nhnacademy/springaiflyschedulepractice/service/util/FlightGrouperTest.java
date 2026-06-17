package com.nhnacademy.springaiflyschedulepractice.service.util;

import com.nhnacademy.springaiflyschedulepractice.dto.FlightInfoResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FlightGrouperTest {
    private FlightGrouper flightGrouper = new FlightGrouper();
    List<FlightInfoResponse> testFlights = List.of(
            FlightInfoResponse.builder()
                    .airlineName("대한항공")
                    .departureTime("202601010900") // 오전 9시
                    .build(),
            FlightInfoResponse.builder()
                    .airlineName("대한항공")
                    .departureTime("202601011400") //오후 2시
                    .build(),
            FlightInfoResponse.builder()
                    .airlineName("아시아나항공")
                    .departureTime("202601011100") //오전 11시
                    .build(),
            FlightInfoResponse.builder()
                    .airlineName("아시아나항공")
                    .departureTime("202601011900") //저녁 7시
                    .build(),
            FlightInfoResponse.builder()
                    .airlineName("제주항공")
                    .departureTime("202601010700") //오전 7시
                    .build(),
            FlightInfoResponse.builder()
                    .airlineName("제주항공")
                    .departureTime("202601012100") //저녁 9시
                    .build()
    );
    @Test
    @DisplayName("항공사명 기반 그룹핑 테스트")
    void groupingByAirlineTest(){

        Map<String, List<FlightInfoResponse>> result = flightGrouper.groupByAirline(testFlights);
        assertAll(
                () -> assertEquals(2, result.get("대한항공").size()),
                () -> assertEquals(2, result.get("아시아나항공").size()),
                () -> assertEquals(2, result.get("제주항공").size())
        );
    }

    @Test
    @DisplayName("시간 기반 그룹핑 테스트")
    void groupingByTimeTesT(){
        Map<String, List<FlightInfoResponse>> result = flightGrouper.groupByTimeSlot(testFlights);
        assertAll(
                () -> assertEquals(3, result.get("오전").size()),
                () -> assertEquals(1, result.get("오후").size()),
                () -> assertEquals(2, result.get("저녁").size())
        );
    }

}