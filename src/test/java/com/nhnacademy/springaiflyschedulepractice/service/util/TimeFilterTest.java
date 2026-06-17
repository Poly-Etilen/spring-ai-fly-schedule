package com.nhnacademy.springaiflyschedulepractice.service.util;

import com.nhnacademy.springaiflyschedulepractice.dto.FlightInfoResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TimeFilterTest {
    private final TimeFilter timeFilter = new TimeFilter();

    @Test
    @DisplayName("자연어 시간 파싱 (오후/오전)")
    void parseNaturalLanguageTime() {
        assertEquals(LocalTime.of(14, 0), timeFilter.parseTime("오후 2시"));
        assertEquals(LocalTime.of(9, 30), timeFilter.parseTime("오전 9시 30분"));
        assertEquals(LocalTime.of(14, 30), timeFilter.parseTime("14:30"));
        assertEquals(LocalTime.of(17, 25), timeFilter.parseTime("1725"));
    }

    @Test
    @DisplayName("유효 인자 검증 테스트 - 항공편 리스트")
    void ValidArgTest1(){
        List<FlightInfoResponse> emptyFlights = new ArrayList<>();

        //유효 항공편 목록이 아닐시 빈리스트 반환
        assertAll(
                () -> assertEquals(0, timeFilter.filterBeforeTime(null, LocalTime.MAX).size()),
                () -> assertEquals(0, timeFilter.filterBeforeTime(emptyFlights, LocalTime.MAX).size()),

                () -> assertEquals(0, timeFilter.filterAfterTime(null, LocalTime.MIN).size()),
                () -> assertEquals(0, timeFilter.filterAfterTime(emptyFlights, LocalTime.MIN).size())
        );
    }

    @Test
    @DisplayName("유효 인자 검증 테스트 - 항공편 출발시간")
    void ValidArgTest2(){
        List<FlightInfoResponse> testFlight = List.of(
                FlightInfoResponse.builder()
                        .departureTime("식별불가 출발시간")
                        .build()
        );

        assertEquals(0, timeFilter.filterBeforeTime(testFlight, LocalTime.MAX).size());
    }

    @Test
    @DisplayName("이후 시간 (ex. ~시 이후 항공편 조회) 필터링 테스트 - 성공 반환값 확인")
    void filterBeforeTimeSuccessTest(){
        List<FlightInfoResponse> testFlights = List.of(
                FlightInfoResponse.builder()
                        .departureTime("202601011400") // 오후 2시
                        .build(),

                FlightInfoResponse.builder()
                        .departureTime("202601011600") // 오후 4시
                        .build()
        );

        //필터링 기준 시간은 오후 3시로함
        assertAll(
                () -> assertEquals(1, timeFilter.filterBeforeTime(testFlights, LocalTime.of(15, 0)).size()),
                () -> assertEquals(testFlights.getFirst(), timeFilter.filterBeforeTime(testFlights, LocalTime.of(15, 0)).getFirst())
        );
    }


    @Test
    @DisplayName("이전 시간 (ex ~시 이전 항공편 조회) 필터링 테스트")
    void filterBeforeTimeTest(){
        List<FlightInfoResponse> testFlights = List.of(
                FlightInfoResponse.builder()
                        .departureTime("202601011400")
                        .build(),

                FlightInfoResponse.builder()
                        .departureTime("202601011600")
                        .build()
        );

        assertAll(
                () -> assertEquals(1, timeFilter.filterAfterTime(testFlights, LocalTime.of(15, 0)).size()),
                () -> assertEquals(testFlights.getLast(), timeFilter.filterAfterTime(testFlights, LocalTime.of(15, 0)).getFirst())
        );
    }
}
