package com.nhnacademy.springaiflyschedulepractice.dto;

public record FlightSearchParam(
        String departure,
        String arrival,
        String data,
        String afterTime,
        String beforeTime,
        Integer minPrice,
        Integer maxPrice
){}
