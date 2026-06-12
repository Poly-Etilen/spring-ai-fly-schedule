package com.nhnacademy.springaiflyschedulepractice.mvc.dto.flight;

import lombok.Data;

@Data
public class FlightSearchRequest {
    private String departureAirport;
    private String arrivalAirport;
    private String date;
    private String afterTime;
    private Integer minPrice;
    private Integer maxPrice;
}
