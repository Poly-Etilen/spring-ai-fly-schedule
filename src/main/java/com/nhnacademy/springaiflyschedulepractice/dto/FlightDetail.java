package com.nhnacademy.springaiflyschedulepractice.dto;

public record FlightDetail(
        String flightId,
        String airlineName,
        String departureTime,
        String arrivalTime,
        Integer price
) {
}
