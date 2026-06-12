package com.nhnacademy.springaiflyschedulepractice.mvc.controller;

import com.nhnacademy.springaiflyschedulepractice.dto.airline.AirlineInfoResponse;
import com.nhnacademy.springaiflyschedulepractice.dto.airport.AirportInfoResponse;
import com.nhnacademy.springaiflyschedulepractice.service.ApiClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/no-llm/info")
@RequiredArgsConstructor
public class InfoController {
    private final ApiClientService apiClientService;

    @GetMapping("/airports")
    public List<AirportInfoResponse> getAirports() {
        return apiClientService.getAirportList();
    }

    @GetMapping("/airports/code")
    public String getAirportByCode(@RequestParam String name) {
        return apiClientService.getAirportList().stream()
                .filter(a -> a.getAirportName().equals(name.trim()))
                .map(AirportInfoResponse::getAirportId)
                .findFirst()
                .orElse("알 수 없는 공항입니다.");
    }

    @GetMapping("/airlines")
    public List<AirlineInfoResponse> getAirlines() {
        return apiClientService.getAirlineList();
    }

    @GetMapping("/airlines/code")
    public String getAirlineByCode(@RequestParam String name) {
        return apiClientService.getAirlineList().stream()
                .filter(a -> a.getAirlineNm().equals(name.trim()))
                .map(AirlineInfoResponse::getAirlineId)
                .findFirst()
                .orElse("알 수 없는 항공사입니다.");
    }
}
