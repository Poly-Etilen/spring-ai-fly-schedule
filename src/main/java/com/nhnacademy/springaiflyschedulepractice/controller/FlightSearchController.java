package com.nhnacademy.springaiflyschedulepractice.controller;

import com.nhnacademy.springaiflyschedulepractice.tool.FlightSearchTool;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/flight")
@RequiredArgsConstructor
public class FlightSearchController {
    private final FlightSearchTool flightSearchTool;

    @GetMapping("/search")
    public String searchFlights(@RequestParam String departure,
                                @RequestParam String arrival,
                                @RequestParam String date,
                                @RequestParam(required = false) String afterTime,
                                @RequestParam(required = false) String beforeTime,
                                @RequestParam(required = false) Integer minPrice,
                                @RequestParam(required = false) Integer maxPrice) {
        var result = flightSearchTool.searchFlightsByAirline(
                departure, arrival, date, afterTime, beforeTime, minPrice, maxPrice);

        StringBuilder builder = new StringBuilder();
        builder.append("항공편 검색 결과: \n\n");

        result.forEach((airline,flights) -> {
            builder.append("[").append(airline).append("]\n");
            flights.forEach(flight -> {
                builder.append(" - ").append(flight.getFlightId())
                        .append(" (").append(flight.getDepartureTime())
                        .append(" -> ").append(flight.getArrivalTime())
                        .append(") ").append(flight.getEconomyCharge()).append("원\n");
            });
            builder.append("\n");
        });
        return builder.toString();
    }
}
