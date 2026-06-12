package com.nhnacademy.springaiflyschedulepractice.mvc.dto.flight;

public record FlightSearchRequest(
        String departureAirport,
        String arrivalAirport,
        String date,
        String afterTime,
        Integer minPrice,
        Integer maxPrice
) {
}
