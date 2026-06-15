package com.nhnacademy.springaiflyschedulepractice.agent;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimeFilterAgentTest {
    private final TimeFilterAgent timeFilterAgent = new TimeFilterAgent();

    @Test
    @DisplayName("자연어 시간 파싱 (오후/오전)")
    void parseNaturalLanguageTime() {
        assertEquals(LocalTime.of(14, 0), timeFilterAgent.parseTime("오후 2시"));
        assertEquals(LocalTime.of(9, 30), timeFilterAgent.parseTime("오전 9시 30분"));
        assertEquals(LocalTime.of(14, 30), timeFilterAgent.parseTime("14:30"));
        assertEquals(LocalTime.of(17, 25), timeFilterAgent.parseTime("1725"));
    }
}
