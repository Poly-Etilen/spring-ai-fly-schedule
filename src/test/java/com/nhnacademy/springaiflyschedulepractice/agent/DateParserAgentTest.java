package com.nhnacademy.springaiflyschedulepractice.agent;

import com.nhnacademy.springaiflyschedulepractice.exception.FlightSearchException;
import com.nhnacademy.springaiflyschedulepractice.service.util.DateParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class DateParserAgentTest {
    private final DateParser dateParser = new DateParser();

    @Test
    @DisplayName("내일 파싱")
    void parseTomorrow() {
        String result = dateParser.parseDate("내일");
        assertNotNull(result);
        assertTrue(result.matches("\\d{8}"));
        assertEquals(LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")), result);
    }

    @Test
    @DisplayName("모레 파싱")
    void parseDayAfterTomorrow() {
        String result = dateParser.parseDate("모레");
        assertNotNull(result);
        assertTrue(result.matches("\\d{8}"));
        assertEquals(LocalDate.now().plusDays(2).format(DateTimeFormatter.ofPattern("yyyyMMdd")), result);
    }

    @Test
    @DisplayName("글피 파싱")
    void parseThreeDaysLater() {
        String result = dateParser.parseDate("글피");
        assertNotNull(result);
        assertTrue(result.matches("\\d{8}"));
        assertEquals(LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("yyyyMMdd")), result);
    }

    @Test
    @DisplayName("특정 날짜 파싱")
    void parseSpecificDate() {
        String result = dateParser.parseDate("2026-11-11");
        assertNotNull(result);
        assertTrue(result.matches("\\d{8}"));
        assertEquals(LocalDate.of(2026,11,11).format(DateTimeFormatter.ofPattern("yyyyMMdd")), result);
    }

    @Test
    @DisplayName("잘못된 날짜 형식 예외")
    void parseWrongDate() {
        assertThrows(FlightSearchException.class, () -> dateParser.parseDate("2026/11/11"));
    }
}
