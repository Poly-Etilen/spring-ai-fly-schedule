package com.nhnacademy.springaiflyschedulepractice.mvc.service;

import com.nhnacademy.springaiflyschedulepractice.dto.FlightInfoResponse;
import com.nhnacademy.springaiflyschedulepractice.exception.ErrorCode;
import com.nhnacademy.springaiflyschedulepractice.exception.FlightSearchException;
import com.nhnacademy.springaiflyschedulepractice.mvc.dto.flight.FlightSearchRequest;
import com.nhnacademy.springaiflyschedulepractice.mvc.dto.flight.FlightSearchResponse;
import com.nhnacademy.springaiflyschedulepractice.service.ApiClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FlightService {
    private final ApiClientService apiClientService;

    public List<FlightSearchResponse> getFilteredFlights(FlightSearchRequest flightSearchRequest) {
        String depCode = convertToAirportCode(flightSearchRequest.getDepartureAirport());
        String arriCode = convertToAirportCode(flightSearchRequest.getArrivalAirport());
        String formattedCate = flightSearchRequest.getDate() != null ? flightSearchRequest.getDate().replace("-", "") : "";

        List<FlightInfoResponse> flights = apiClientService.getFlightSchedule(depCode, arriCode, formattedCate);
        return flights.stream()
                .filter(flight -> checkTimeFilter(flight.getDepartureTime(), flightSearchRequest.getAfterTime()))
                .filter(flight -> checkPriceFilter(flight.getEconomyCharge(), flightSearchRequest.getMinPrice(), flightSearchRequest.getMaxPrice()))
                .map(this::convertToResponseDto)
                .toList();
    }

    private FlightSearchResponse convertToResponseDto(FlightInfoResponse flightInfoResponse) {
        return FlightSearchResponse.builder()
                .flightId(flightInfoResponse.getFlightId())
                .airlineName(flightInfoResponse.getAirlineName())
                .departureTime(flightInfoResponse.getDepartureTime())
                .arrivalTime(flightInfoResponse.getArrivalTime())
                .price(flightInfoResponse.getEconomyCharge())
                .build();
    }

    private boolean checkPriceFilter(String apiPrice, Integer minPrice, Integer maxPrice) {
        if ((minPrice == null && maxPrice == null) || apiPrice == null || apiPrice.isBlank()) {
            return true;
        }
        try {
            int price = Integer.parseInt(apiPrice.replaceAll("[^0-9]", ""));
            if (price == 0) return false;
            if (minPrice != null && price < minPrice) return false;
            if (maxPrice != null && price > maxPrice) return false;
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean checkTimeFilter(String apiDepartureTime, String afterTimeParam) {
        if (afterTimeParam == null || afterTimeParam.isBlank()) return true;
        try {
            String cleanAfterTime = afterTimeParam.replace(":", "");
            LocalTime afterTime = LocalTime.parse(cleanAfterTime, DateTimeFormatter.ofPattern("HHmm"));
            if (apiDepartureTime != null && apiDepartureTime.length() >= 12) {
                String timePart = apiDepartureTime.substring(8, 12);
                LocalTime flightTime = LocalTime.parse(timePart, DateTimeFormatter.ofPattern("HHmm"));
                return !flightTime.isBefore(afterTime);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private String convertToAirportCode(String airportName) {
        if (airportName == null) return "NAARKJJ";
        return switch (airportName.trim()) {
            case "김포" -> "NAARKSS";
            case "인천" -> "NAARKSI";
            case "제주" -> "NAARKPC";
            case "광주" -> "NAARKJJ";
            case "김해", "부산" -> "NAARKPK";
            case "청주" -> "NAARKTU";
            case "대구" -> "NAARKTN";
            case "무안" -> "NAARKJB";
            case "여수" -> "NAARKJY";
            case "울산" -> "NAARKPU";
            case "양양" -> "NAARKNY";
            case "포항" -> "NAARKTH";
            default -> throw new FlightSearchException(ErrorCode.INVALID_AIRPORT_NAME, "지원하지 않는 공항입니다. : " + airportName);
        };
    }
}
