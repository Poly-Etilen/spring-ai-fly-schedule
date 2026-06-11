package com.nhnacademy.springaiflyschedulepractice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    MISSING_AIRPORT_NAME(HttpStatus.BAD_REQUEST, "공항 이름을 입력해주세요."),
    INVALID_AIRPORT_NAME(HttpStatus.BAD_REQUEST, "지원하지 않거나 알 수 없는 공항입니다."),
    INVALID_DATE_FORMAT(HttpStatus.BAD_REQUEST, "날짜 형식이 올바르지 않습니다. (예: 내일, 모레, YYYY-MM-DD 등)"),
    MISSING_TIME_INPUT(HttpStatus.BAD_REQUEST, "시간을 입력해주세요."),
    INVALID_TIME_FORMAT(HttpStatus.BAD_REQUEST, "시간 형식이 올바르지 않습니다."),
    MISSING_ROUTE_INFO(HttpStatus.BAD_REQUEST, "출발 공항과 도착 공항을 명확히 입력해주세요"),

    API_CALL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "공공데이터 API 호출 중 오류가 발생했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;
}
