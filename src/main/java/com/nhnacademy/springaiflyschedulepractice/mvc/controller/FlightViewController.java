package com.nhnacademy.springaiflyschedulepractice.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FlightViewController {

    @GetMapping("/flights/search")
    public String flightSearchPage() {
        return "no-llm-search";
    }
}
