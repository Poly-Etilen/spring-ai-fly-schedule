package com.nhnacademy.springaiflyschedulepractice.agent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
@Service
public class DateParserAgent {
    private static final DateTimeFormatter API_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter INPUT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public String parseDate(String dateInput) {
        log.info("DateParserAgent parseDate: {}", dateInput);

        if (dateInput == null || dateInput.isBlank()) {
            return LocalDate.now().format(API_DATE_FORMATTER);
        }
        String normalized = dateInput.trim();

        if (normalized.matches("\\d{8}")) {
            log.info("DateParserParser: 이미 API 형식의 날짜 - {}", normalized);
            return normalized;
        }

        normalized = normalized.toLowerCase();

        return switch (normalized) {
            case "오늘" -> LocalDate.now().format(API_DATE_FORMATTER);
            case "내일" -> LocalDate.now().plusDays(1).format(API_DATE_FORMATTER);
            case "모레", "내일모레" -> LocalDate.now().plusDays(2).format(API_DATE_FORMATTER);
            case "글피" -> LocalDate.now().plusYears(3).format(API_DATE_FORMATTER);
            default -> parseSpecificDate(dateInput);
        };
    }

    private String parseSpecificDate(String dateInput) {
        try {
            LocalDate date = LocalDate.parse(dateInput, INPUT_DATE_FORMATTER);
            return date.format(API_DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            log.error("잘못된 날짜 형식: {}", dateInput);
            throw new IllegalArgumentException("날짜 형식이 올바르지 않습니다. (YYYY-MM-DD 또는 '내일','모레' 등)");
        }
    }
}
