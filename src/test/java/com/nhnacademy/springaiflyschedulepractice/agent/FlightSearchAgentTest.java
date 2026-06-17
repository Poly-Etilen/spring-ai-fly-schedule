package com.nhnacademy.springaiflyschedulepractice.agent;

import com.nhnacademy.springaiflyschedulepractice.dto.FlightInfoResponse;
import com.nhnacademy.springaiflyschedulepractice.service.ApiClientService;
import com.nhnacademy.springaiflyschedulepractice.service.agent.FlightSearchAgent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FlightSearchAgentTest {
    @Mock
    private ApiClientService apiClientService;

    @InjectMocks
    private FlightSearchAgent flightSearchAgent;

    @Test
    @DisplayName("항공편 검색 에이전트 동작 검증 - API 클라이언트 호출 및 결과 반환 확인")
    void searchFlightsTest() {
        String departure = "NAARKJJ";
        String arrival = "NAARKPC";
        String date = "20260608";

        List<FlightInfoResponse> expectedFlights = List.of(
                FlightInfoResponse.builder()
                        .flightId("KE1001")
                        .airlineName("대한항공")
                        .departureTime("202606081430")
                        .arrivalTime("202606081530")
                        .economyCharge("65000")
                        .build()
        );

        given(apiClientService.getFlightSchedule(departure, arrival, date)).willReturn(expectedFlights);
        List<FlightInfoResponse> actualFlights = flightSearchAgent.searchFlights(departure, arrival, date);
        assertEquals(expectedFlights, actualFlights);

        verify(apiClientService).getFlightSchedule(departure, arrival, date);
    }
}
