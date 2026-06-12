package com.nhnacademy.springaiflyschedulepractice.mvc.controller;

import com.nhnacademy.springaiflyschedulepractice.mvc.dto.flight.FlightSearchRequest;
import com.nhnacademy.springaiflyschedulepractice.mvc.dto.flight.FlightSearchResponse;
import com.nhnacademy.springaiflyschedulepractice.mvc.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/no-llm")
@RequiredArgsConstructor
public class FlightController {
    private final FlightService flightService;

    @GetMapping("/flights")
    public List<FlightSearchResponse> searchFlight(@ModelAttribute FlightSearchRequest request) {
        return flightService.getFilteredFlights(request);
    }
}
