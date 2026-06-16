package com.nhnacademy.springaiflyschedulepractice.exception;

import com.nhnacademy.springaiflyschedulepractice.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(FlightSearchException.class)
    public ResponseEntity<ApiResponse<Void>> handleFlightSearchException(FlightSearchException e) {
        log.warn("비즈니스 예외 발생: {}", e.getMessage());
        return ResponseEntity.status(e.getErrorCode().getStatus()).body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("서버 내부 오류 발생", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("필수 파라미터 부재", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
    }
}
