package com.nhnacademy.springaiflyschedulepractice.agent;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class DateParserAgentTest {
    private final DateParserAgent dateParserAgent = new DateParserAgent();

    @Test
    @DisplayName("내일 파싱")
    void parseTomorrow() {
        String result = dateParserAgent.parseDate("내일");
        assertNotNull(result);
        assertTrue(result.matches("\\d{8}"));
        assertEquals(LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")), result);
    }

    @Test
    @DisplayName("모레 파싱")
    void parseDayAfterTomorrow() {
        String result = dateParserAgent.parseDate("모레");
        assertNotNull(result);
        assertTrue(result.matches("\\d{8}"));
        assertEquals(LocalDate.now().plusDays(2).format(DateTimeFormatter.ofPattern("yyyyMMdd")), result);
    }

    @Test
    @DisplayName("특정 날짜 파싱")
    void parseSpecificDate() {
        String result = dateParserAgent.parseDate("2026-11-11");
        assertNotNull(result);
        assertTrue(result.matches("\\d{8}"));
        assertEquals(LocalDate.of(2026,11,11).format(DateTimeFormatter.ofPattern("yyyyMMdd")), result);
    }

    @Test
    @DisplayName("잘못된 날짜 형식 예외")
    void parseWrongDate() {
        assertThrows(IllegalArgumentException.class, () -> dateParserAgent.parseDate("2026/11/11"));
    }
}
