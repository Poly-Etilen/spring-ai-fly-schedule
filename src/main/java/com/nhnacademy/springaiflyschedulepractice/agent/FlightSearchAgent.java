package com.nhnacademy.springaiflyschedulepractice.agent;

import com.nhnacademy.springaiflyschedulepractice.dto.FlightDetail;
import com.nhnacademy.springaiflyschedulepractice.dto.FlightInfoResponse;
import com.nhnacademy.springaiflyschedulepractice.dto.FlightSearchParam;
import com.nhnacademy.springaiflyschedulepractice.dto.airline.AirlineGroup;
import com.nhnacademy.springaiflyschedulepractice.service.ApiClientService;
import com.nhnacademy.springaiflyschedulepractice.service.ParameterNormalizerAgent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlightSearchAgent {
    private final ApiClientService apiClientService;

    public List<FlightInfoResponse> searchFlights(String departure, String arrival, String date) {
        log.info("FlightSearchAgent: 항공편 API 검색 실행 ({} -> {}, 날짜: {}", departure, arrival, date);
        return apiClientService.getFlightSchedule(departure, arrival, date);
    }
}
