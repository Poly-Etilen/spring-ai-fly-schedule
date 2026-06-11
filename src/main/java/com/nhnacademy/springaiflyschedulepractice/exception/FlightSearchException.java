package com.nhnacademy.springaiflyschedulepractice.exception;

import lombok.Getter;

@Getter
public class FlightSearchException extends RuntimeException {
    private final ErrorCode errorCode;
    public FlightSearchException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public FlightSearchException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
