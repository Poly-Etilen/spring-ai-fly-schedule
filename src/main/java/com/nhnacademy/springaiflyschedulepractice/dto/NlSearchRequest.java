package com.nhnacademy.springaiflyschedulepractice.dto;

import lombok.Data;

@Data
public class NlSearchRequest {
    private String message;
    private String model = "gemini";
    private String type = "orchestrator";
}
