package com.nhnacademy.springaiflyschedulepractice.service.util;

import com.nhnacademy.springaiflyschedulepractice.exception.ErrorCode;
import com.nhnacademy.springaiflyschedulepractice.exception.FlightSearchException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AirportCodeConverter {
    private static final Map<String, String> AIRPORT_CODE_MAP = new HashMap<>();

    static {
        // 수도권
        AIRPORT_CODE_MAP.put("김포", "NAARKSS");
        AIRPORT_CODE_MAP.put("인천", "NAARKSI");

        // 부산/경남권
        AIRPORT_CODE_MAP.put("김해", "NAARKPK");
        AIRPORT_CODE_MAP.put("부산", "NAARKJB");
        AIRPORT_CODE_MAP.put("울산", "NAARKPU");

        // 호남권
        AIRPORT_CODE_MAP.put("광주", "NAARKJJ");
        AIRPORT_CODE_MAP.put("여수", "NAARKJY");
        AIRPORT_CODE_MAP.put("무안", "NAARKJB");

        // 영남권
        AIRPORT_CODE_MAP.put("대구", "NAARKTN");
        AIRPORT_CODE_MAP.put("포항", "NAARKTH");

        // 충청/강원권
        AIRPORT_CODE_MAP.put("청주", "NAARKTU");
        AIRPORT_CODE_MAP.put("양양", "NAARKNY");

        // 제주권
        AIRPORT_CODE_MAP.put("제주", "NAARKPC");
    }

    public String getAirportCode(String airportName) {

        validateAirport(airportName);
        String normalized = airportName.trim();

        if (normalized.matches("NAARK[A-Z]{2}")) {
            log.info("공항 코드 직접 입력됨: {}", normalized);
            return normalized;
        }

        String code = AIRPORT_CODE_MAP.get(normalized);
        if (code == null) {
            log.warn("알 수 없는 공항: {}", airportName);
            throw new FlightSearchException(ErrorCode.INVALID_AIRPORT_NAME, "알 수 없는 공항입니다: " + airportName);
        }
        log.info("공항 코드 변환: {} -> {}", airportName, code);
        return code;
    }

    public void validateAirport(String airportName) {
        if (airportName == null || airportName.isBlank()) {
            throw new FlightSearchException(ErrorCode.MISSING_AIRPORT_NAME);
        }
        String normalized = airportName.trim();
        if (!normalized.matches("NAARK[A-Z]{2}") && !AIRPORT_CODE_MAP.containsKey(normalized)) {
            log.warn("알 수 없는 공항 요청: {}", airportName);
            throw new FlightSearchException(ErrorCode.MISSING_AIRPORT_NAME, "알 수 없는 공항입니다.: " + airportName);
        }
    }

}
