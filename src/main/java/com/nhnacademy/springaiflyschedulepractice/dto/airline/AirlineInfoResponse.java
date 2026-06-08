package com.nhnacademy.springaiflyschedulepractice.dto.airline;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AirlineInfoResponse {
    @JsonProperty("airlineId")
    private String airlineId;

    @JsonProperty("airlineNm")
    private String airlineNm;
}
