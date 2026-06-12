package com.nhnacademy.springaiflyschedulepractice.mvc.dto.flight;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FlightSearchResponse {
    private String flightId;
    private String departureTime;
    private String arrivalTime;
    private String price;
}
