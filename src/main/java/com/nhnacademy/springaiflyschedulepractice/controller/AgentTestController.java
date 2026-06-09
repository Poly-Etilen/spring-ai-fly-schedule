package com.nhnacademy.springaiflyschedulepractice.controller;

import com.nhnacademy.springaiflyschedulepractice.agent.AirportCodeAgent;
import com.nhnacademy.springaiflyschedulepractice.agent.DateParserAgent;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
public class AgentTestController {
    private final DateParserAgent dateParserAgent;
    private final AirportCodeAgent airportCodeAgent;

    @GetMapping("/date-parse")
    public String testDateParser(@RequestParam String input) {
        try {
            String result = dateParserAgent.parseDate(input);
            return result;
        } catch (Exception e) {
            return "파싱 실패: " + e.getMessage();
        }
    }

    @GetMapping("/airport-code")
    public String testAirportCode(@RequestParam String input) {
        try {
            String result = airportCodeAgent.getAirportCode(input);
            return "공항 코드 변환: " + input + " -> " + result;
        } catch (Exception e) {
            return "변환 실패: " + e.getMessage();
        }
    }

    @GetMapping("/chain")
    public String testChain(
            @RequestParam String departure,
            @RequestParam String arrival,
            @RequestParam String date
    ) {
        try {
            String depCode = airportCodeAgent.getAirportCode(departure);
            String arrCode = airportCodeAgent.getAirportCode(arrival);
            String formattedDate = dateParserAgent.parseDate(date);

            return String.format(
                    "에이전트 체이닝 결과:\n" +
                            "출발: %s -> %s\n" +
                            "도착: %s -> %s\n" +
                            "날짜: %s -> %s",
                    departure,depCode,
                    arrival, arrCode,
                    date,formattedDate
            );
        } catch (Exception e) {
            return "처리 실패: " + e.getMessage();
        }
    }
}
