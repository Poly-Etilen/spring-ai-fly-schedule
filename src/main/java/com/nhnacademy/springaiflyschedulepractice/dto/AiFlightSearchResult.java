package com.nhnacademy.springaiflyschedulepractice.dto;

import com.nhnacademy.springaiflyschedulepractice.dto.airline.AirlineGroup;
import org.springframework.context.annotation.Description;

import java.util.List;

public record AiFlightSearchResult(
        // 자바코드에는 영향이 없고 AI한테만 영향을 줌.
        @Description("API 요청 처리 성공 여부 (성공 시 true, 에러 발생 시 false)") boolean success,
        @Description("결과에 대한 설명 또는 에러 발생 시의 상세 에러 메시지") String message,
        @Description("사용자 요청에서 추출된 검색 조건 (출발지, 도착지, 날짜 등)") FlightSearchParam param,
        @Description("항공사별로 그룹화된 실제 항공편 검색 결과 리스트") List<AirlineGroup> data
) {
    public static AiFlightSearchResult success(FlightSearchParam param, List<AirlineGroup> data) {
        return new AiFlightSearchResult(true, "성공", param, data);
    }

    public static AiFlightSearchResult error(String message) {
        return new AiFlightSearchResult(false, message, null, null);
    }
}
