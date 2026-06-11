package com.nhnacademy.springaiflyschedulepractice.controller;


import com.nhnacademy.springaiflyschedulepractice.dto.FlightDetail;
import com.nhnacademy.springaiflyschedulepractice.dto.FlightInfoResponse;
import com.nhnacademy.springaiflyschedulepractice.dto.airline.AirlineGroup;
import com.nhnacademy.springaiflyschedulepractice.service.MultiAgentOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/coordinator")
@RequiredArgsConstructor
public class CoordinatorTestController {
    private final MultiAgentOrchestrator coordinator;

    /**
     * 기본 검색 테스트
     * GET /api/coordinator/search?departure=광주&arrival=제주&date=내일
     */
    @GetMapping("/search")
    public String search(
            @RequestParam String departure,
            @RequestParam String arrival,
            @RequestParam String date) {

        List<AirlineGroup> result = coordinator.basicSearch(departure, arrival, date);

//        StringBuilder sb = new StringBuilder();
//        sb.append("조율된 검색 결과:\n\n");
//
//        result.forEach((airline, flights) -> {
//            sb.append("[").append(airline).append("] - ").append(flights.size()).append("편\n");
//            flights.forEach(f -> {
//                sb.append("  ").append(f.getFlightId())
//                        .append(" (").append(f.getDepartureTime())
//                        .append(" → ").append(f.getArrivalTime())
//                        .append(") ").append(f.getEconomyCharge()).append("원\n");
//            });
//            sb.append("\n");
//        });

        return formatResult("조율된 검색 결과", result);
    }

    /**
     * 시간 필터 테스트
     * GET /api/coordinator/search/time?departure=광주&arrival=제주&date=내일&afterTime=14:00
     */
    @GetMapping("/search/time")
    public String searchWithTimeFilter(
            @RequestParam String departure,
            @RequestParam String arrival,
            @RequestParam String date,
            @RequestParam String afterTime) {

        List<AirlineGroup> result = coordinator.timeFilterSearch(departure, arrival, date, afterTime);

        return formatResult("시간 필터 결과 (" + afterTime + " 이후)", result);
    }

    /**
     * 가격 필터 테스트
     * GET /api/coordinator/search/price?departure=광주&arrival=제주&date=내일&minPrice=30000&maxPrice=70000
     */
    @GetMapping("/search/price")
    public String searchWithPriceFilter(
            @RequestParam String departure,
            @RequestParam String arrival,
            @RequestParam String date,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice) {

        List<AirlineGroup> result = coordinator.priceFilterSearch(departure, arrival, date, minPrice, maxPrice);

        return formatResult("가격 필터 결과 (" + minPrice + "~" + maxPrice + "원)", result);
    }

    private String formatResult(String title, List<AirlineGroup> result) {
        StringBuilder sb = new StringBuilder();
        sb.append(title).append(":\n\n");

        int totalFlights = 0;

        for (AirlineGroup group : result) {
            List<FlightDetail> flights = group.flights();
            totalFlights += flights.size();

            sb.append("[").append(group.airlineName()).append("] - ").append(flights.size()).append("편\n");

            for (FlightDetail f : flights) {
                sb.append("  ").append(f.flightId())
                        .append(" (").append(f.departureTime())
                        .append(" → ").append(f.arrivalTime())
                        .append(") ").append(f.price() != null ? f.price() : "가격 미상").append("원\n");
            }
            sb.append("\n");
        }

        sb.append("총 ").append(totalFlights).append("편의 항공편이 있습니다.");
        return sb.toString();
    }
}