package com.nhnacademy.springaiflyschedulepractice.dto;

public record FlightSearchParam(
        String departure,
        String arrival,
        String date,
        String afterTime,
        String beforeTime,
        Integer minPrice,
        Integer maxPrice
){}
