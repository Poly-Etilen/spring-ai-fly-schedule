package com.nhnacademy.springaiflyschedulepractice.service.util;

import com.nhnacademy.springaiflyschedulepractice.dto.FlightInfoResponse;
import com.nhnacademy.springaiflyschedulepractice.exception.ErrorCode;
import com.nhnacademy.springaiflyschedulepractice.exception.FlightSearchException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TimeFilter {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HHmm");

    public LocalTime parseTime(String timeInput){
        if(timeInput == null || timeInput.isBlank()){
            throw new FlightSearchException(ErrorCode.MISSING_TIME_INPUT);
        }

        String normalized = timeInput.trim().toLowerCase();

        if(normalized.contains("오후")){
            String number = normalized.replaceAll("[^0-9]", "");

            if(!number.isEmpty()){
                LocalTime time = parseNumericTime(number);
                int hour = time.getHour();

                if(hour < 12){
                    hour += 12;
                }

                if(hour >= 24){
                    hour = 12;
                }
                return LocalTime.of(hour, time.getMinute());
            }
        }

        if(normalized.contains("오전")){
            String number = normalized.replaceAll("[^0-9]", "");

            if(!number.isEmpty()){
                LocalTime time = parseNumericTime(number);
                int hour = time.getHour();

                if(hour == 12){
                    hour = 0;
                }
                return LocalTime.of(hour, time.getMinute());
            }
        }

        try {
            String cleaned = normalized.replaceAll("[^0-9]", "");
            return parseNumericTime(cleaned);
        } catch (RuntimeException e) {
            throw new FlightSearchException(ErrorCode.INVALID_TIME_FORMAT, "시간 형식이 올바르지 않습니다. 입력값: " + timeInput);
        }
    }

    private LocalTime parseNumericTime(String value) {
        if (value == null || value.isBlank()) {
            throw new DateTimeParseException("empty time", "", 0);
        }

        String cleaned = value.replaceAll("[^0-9]", "");
        if (cleaned.length() == 1 || cleaned.length() == 2) {
            cleaned = String.format("%02d00", Integer.parseInt(cleaned));
        } else if (cleaned.length() == 3) {
            cleaned = "0" + cleaned;
        }

        return LocalTime.parse(cleaned, TIME_FORMATTER);
    }

    public List<FlightInfoResponse> filterAfterTime(List<FlightInfoResponse> flights, LocalTime afterTime){
        if(flights == null || flights.isEmpty()){
            return List.of();
        }

        return flights.stream()
                .filter(flight -> {
                    try {
                        LocalTime departureTime = parseDepartureTime(flight.getDepartureTime());
                        return !departureTime.isBefore(afterTime);
                    } catch (Exception e) {
                        log.warn("시간 파싱 실패: {}", flight.getDepartureTime());
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }

    public List<FlightInfoResponse> filterBeforeTime(List<FlightInfoResponse> flights, LocalTime beforeTime){
        if(flights == null || flights.isEmpty()){
            return List.of();
        }
        return flights.stream()
                .filter(flight -> {
                    try {
                        LocalTime departureTime = parseDepartureTime(flight.getDepartureTime());
                        return !departureTime.isAfter(beforeTime);
                    } catch (Exception e) {
                        log.warn("시간 파싱 실패: {}", flight.getDepartureTime());
                        return false;
                    }
                })
                .toList();
    }

    private LocalTime parseDepartureTime(String departureTime) {
        if (departureTime == null || departureTime.length() < 8) {
            throw new FlightSearchException(ErrorCode.INVALID_TIME_FORMAT, "잘못된 출발 시간 형식: " + departureTime);
        }


        String timePart = departureTime.substring(8, 12);
        return LocalTime.parse(timePart, TIME_FORMATTER);
    }
}
