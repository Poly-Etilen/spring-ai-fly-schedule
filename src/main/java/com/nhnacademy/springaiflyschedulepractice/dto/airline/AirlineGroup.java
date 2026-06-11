package com.nhnacademy.springaiflyschedulepractice.dto.airline;

import com.nhnacademy.springaiflyschedulepractice.dto.FlightDetail;

import java.util.List;

public record AirlineGroup(
        String airlineName,
        List<FlightDetail> flights
) { }
