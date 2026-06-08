package com.nhnacademy.springaiflyschedulepractice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightInfoResponse {
    @JsonProperty("arrAirportNm")
    private String arrivalAirport;

    @JsonProperty("vihicleId")
    private String flightId;

    @JsonProperty("airlineNm")
    private String airlineName;

    @JsonProperty("depPlandTime")
    private String departureTime;

    @JsonProperty("arrPlandTime")
    private String arrivalTime;

    @JsonProperty("economyCharge")
    private String economyCharge;

    @JsonProperty("prestigeCharge")
    private String prestigeCharge;

    @JsonProperty("depAirportNm")
    private String departureAirport;

}
