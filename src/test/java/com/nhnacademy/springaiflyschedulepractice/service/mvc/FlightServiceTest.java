package com.nhnacademy.springaiflyschedulepractice.service.mvc;

import com.nhnacademy.springaiflyschedulepractice.dto.FlightInfoResponse;
import com.nhnacademy.springaiflyschedulepractice.exception.FlightSearchException;
import com.nhnacademy.springaiflyschedulepractice.mvc.dto.flight.FlightSearchRequest;
import com.nhnacademy.springaiflyschedulepractice.mvc.dto.flight.FlightSearchResponse;
import com.nhnacademy.springaiflyschedulepractice.mvc.service.FlightService;
import com.nhnacademy.springaiflyschedulepractice.service.ApiClientService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FlightServiceTest {
    @Mock
    private ApiClientService apiClientService;

    @InjectMocks
    private FlightService flightService;

    @Test
    @DisplayName("필터가 없는 기본 검색 성공 (정상 파라미터 및 DTO 변환 검증)")
    void getFilteredFlightsSuccessNoFilters() {
        FlightSearchRequest request = new FlightSearchRequest();
        request.setDepartureAirport("광주");
        request.setArrivalAirport("제주");
        request.setDate("2026-06-20");

        FlightInfoResponse mockInfoResponse = FlightInfoResponse.builder()
                .flightId("KE1234")
                .airlineName("대한항공")
                .departureTime("202606201400")
                .arrivalTime("202607071800")
                .economyCharge("65000")
                .build();

        given(apiClientService.getFlightSchedule("NAARKJJ", "NAARKPC", "20260620"))
                .willReturn(List.of(mockInfoResponse));
        List<FlightSearchResponse> result = flightService.getFilteredFlights(request);
        assertEquals(1, result.size());
        assertEquals("KE1234", result.getFirst().getFlightId());
        assertEquals("대한항공", result.getFirst().getAirlineName());
        assertEquals("202606201400", result.getFirst().getDepartureTime());
    }

    @Test
    @DisplayName("시간 필터 적용 검증 (afterTime 이후 항공편만 반환)")
    void getFilteredFlightsWithTimeFilter() {
        FlightSearchRequest request = new FlightSearchRequest();
        request.setDepartureAirport("광주");
        request.setArrivalAirport("제주");
        request.setDate("2026-06-20");
        request.setAfterTime("14:00");

        FlightInfoResponse earlyFlight = FlightInfoResponse.builder()
                .flightId("KE1234")
                .departureTime("202606201200")
                .build();
        FlightInfoResponse lateFlight = FlightInfoResponse.builder()
                .flightId("KE5678")
                .departureTime("202606201420")
                .build();

        given(apiClientService.getFlightSchedule("NAARKJJ", "NAARKPC", "20260620")).willReturn(List.of(earlyFlight, lateFlight));
        List<FlightSearchResponse> result = flightService.getFilteredFlights(request);

        assertEquals(1, result.size());
        assertEquals("KE5678", result.getFirst().getFlightId());
    }

    @Test
    @DisplayName("가격 필터 적용 검증 (minPrice, maxPrice ㅂㅁ위 내 항공편만 반환)")
    void getFilteredFlightsWithPriceFilter() {
        FlightSearchRequest request = new FlightSearchRequest();
        request.setDepartureAirport("광주");
        request.setArrivalAirport("제주");
        request.setDate("2026-08-08");
        request.setMinPrice(40000);
        request.setMaxPrice(70000);

        FlightInfoResponse cheap = FlightInfoResponse.builder()
                .flightId("F1")
                .economyCharge("30000")
                .build();
        FlightInfoResponse match = FlightInfoResponse.builder()
                .flightId("F2")
                .economyCharge("50,000")
                .build();
        FlightInfoResponse exp = FlightInfoResponse.builder()
                .flightId("F3")
                .economyCharge("80000")
                .build();

        given(apiClientService.getFlightSchedule("NAARKJJ", "NAARKPC", "20260808")).willReturn(List.of(cheap, match, exp));
        List<FlightSearchResponse> result = flightService.getFilteredFlights(request);

        assertEquals(1, result.size());
        assertEquals("F2", result.getFirst().getFlightId());
        assertEquals("50,000", result.getFirst().getPrice());
    }

    @Test
    @DisplayName("지원하지 않는 공항 이름 입력 시 예외 발생 검증")
    void getFilteredFlightsInvalidAirport() {
        FlightSearchRequest request = new FlightSearchRequest();
        request.setDepartureAirport("없는공항");
        request.setArrivalAirport("제주");

        assertThrows(FlightSearchException.class, () -> flightService.getFilteredFlights(request));
    }

    @Test
    @DisplayName("출발지 null 입력 시 디폴트로 광주 공항(NAARKJJ) 코드 사용 검증")
    void getFilteredFlightsNullDepartureAirport() {
        FlightSearchRequest request = new FlightSearchRequest();
        request.setDepartureAirport(null);
        request.setArrivalAirport("제주");
        request.setDate("2026-08-08");

        given(apiClientService.getFlightSchedule("NAARKJJ", "NAARKPC", "20260808"))
                .willReturn(List.of());

        List<FlightSearchResponse> result = flightService.getFilteredFlights(request);
        assertEquals(0, result.size());
    }
}
