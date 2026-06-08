package com.nhnacademy.springaiflyschedulepractice.dto.airport;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AirportInfoResponse {
    @JsonProperty("airportId")
    private String airportId;

    @JsonProperty("airportNm")
    private String airportName;
}
