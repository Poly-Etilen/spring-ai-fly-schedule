package com.nhnacademy.springaiflyschedulepractice.agent;

import com.nhnacademy.springaiflyschedulepractice.dto.FlightInfoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TimerFilterAgent {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HHmm");

    public LocalTime parseTime(String timeInput){
        if(timeInput == null || timeInput.isBlank()){
            throw new IllegalArgumentException("시간을 입력해주세요");
        }

        String normalized = timeInput.trim().toLowerCase();

        if(normalized.contains("오후")){
            String number = normalized.replaceAll("[^0-9]", "");

            if(!number.isEmpty()){
                int hour = Integer.parseInt(number);

                if(hour < 12){
                    hour += 12;
                }

                if(hour >= 24){
                    hour = 12;
                }
                return LocalTime.of(hour, 0);
            }
        }

        if(normalized.contains("오전")){
            String number = normalized.replaceAll("[^0-9]", "");

            if(!number.isEmpty()){
                int hour = Integer.parseInt(number);

                if(hour == 12){
                    hour = 0;
                }
                return LocalTime.of(hour, 0);
            }
        }

        try {
            String cleaned = normalized.replaceAll("[^0-9]", "");
            if (cleaned.length() == 1 || cleaned.length() == 2) {
                cleaned = String.format("%02d00", Integer.parseInt(cleaned));
            }
            return LocalTime.parse(cleaned, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("시간 형식이 올바르지 않습니다. (HH:mm 또는 '오후 2시' 등)");
        }
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

    private LocalTime parseDepartureTime(String departureTime) {
        if (departureTime == null || departureTime.length() < 8) {
            throw new IllegalArgumentException("잘못된 출발 시간 형식");
        }

        // "202503090955" 형식에서 시간 부분(0955) 추출
        String timePart = departureTime.substring(8, 12);
        return LocalTime.parse(timePart, TIME_FORMATTER);
    }
}
