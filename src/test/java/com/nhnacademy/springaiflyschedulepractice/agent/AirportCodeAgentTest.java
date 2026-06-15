package com.nhnacademy.springaiflyschedulepractice.agent;

import com.nhnacademy.springaiflyschedulepractice.exception.FlightSearchException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AirportCodeAgentTest {
    private final AirportCodeAgent airportCodeAgent = new AirportCodeAgent();

    @Test
    @DisplayName("광주 공항 코드 변환")
    void getGwangjuCode() {
        String code = airportCodeAgent.getAirportCode("광주");
        assertNotNull(code);
        assertEquals("NAARKJJ", code);
    }

    @Test
    @DisplayName("제주 공항 코드 변환")
    void getJejuCode() {
        String code = airportCodeAgent.getAirportCode("제주");
        assertNotNull(code);
        assertEquals("NAARKPC", code);
    }

    @Test
    @DisplayName("이미 공항 코드인 경우")
    void getCodeAlreadyCoded() {
        String code = airportCodeAgent.getAirportCode("NAARKJJ");
        assertNotNull(code);
        assertEquals("NAARKJJ", code);
    }

    @Test
    @DisplayName("알 수 없는 공항 예외")
    void getCodeNotExist() {
        assertThrows(FlightSearchException.class, () -> airportCodeAgent.getAirportCode("평양"));
    }

    @Test
    @DisplayName("null 또는 빈 문자열 입력 시 예외 발생")
    void getCodeNullOrEmpty() {
        assertThrows(FlightSearchException.class, () -> airportCodeAgent.getAirportCode(null));
        assertThrows(FlightSearchException.class, () -> airportCodeAgent.getAirportCode("    "));
    }
}
