package com.nhnacademy.springaiflyschedulepractice.dto.airport;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nhnacademy.springaiflyschedulepractice.dto.FlightInfoResponse;
import lombok.Data;

import java.util.List;

@Data
public class AirportResponseWrapper {
    @JsonProperty("response")
    private ApiResponse apiResponse;

    @Data
    public static class ApiResponse {
        @JsonProperty("body")
        private ResponseBody body;
    }

    @Data
    public static class ResponseBody {
        @JsonProperty("items")
        private Items items;
    }

    @Data
    public static class Items {
        @JsonProperty("item")
        private List<AirportInfoResponse> item;
    }
}
