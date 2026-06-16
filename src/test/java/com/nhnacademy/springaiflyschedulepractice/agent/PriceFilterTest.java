package com.nhnacademy.springaiflyschedulepractice.agent;

import com.nhnacademy.springaiflyschedulepractice.dto.FlightInfoResponse;
import com.nhnacademy.springaiflyschedulepractice.service.util.PriceFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PriceFilterTest {
    private final PriceFilter priceFilter = new PriceFilter();

    @Test
    @DisplayName("가격 필터링 - 최소 및 최대 가격 조건이 모두 있을 때")
    void filterByMinAndMaxPrice() {
        List<FlightInfoResponse> flights = List.of(
                FlightInfoResponse.builder().flightId("FL1").economyCharge("30000").build(),
                FlightInfoResponse.builder().flightId("FL2").economyCharge("50,000").build(),
                FlightInfoResponse.builder().flightId("FL3").economyCharge("70000원").build()
        );
        List<FlightInfoResponse> result = priceFilter.filterByPriceRange(flights, 40000, 60000);

        assertEquals(1, result.size());
        assertEquals("FL2", result.getFirst().getFlightId());
    }

    @Test
    @DisplayName("가격 정보가 Null 이거나 파싱 불가능한 경우 제외")
    void filterWithInvalidPrice() {
        List<FlightInfoResponse> flights = List.of(
                FlightInfoResponse.builder().flightId("FL1").economyCharge(null).build(),
                FlightInfoResponse.builder().flightId("FL2").economyCharge("가격미상").build(),
                FlightInfoResponse.builder().flightId("FL3").economyCharge("").build()
        );
        List<FlightInfoResponse> result = priceFilter.filterByPriceRange(flights, 0, 100_000);
        assertTrue(result.isEmpty());
    }
}
