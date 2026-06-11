package com.nhnacademy.springaiflyschedulepractice.dto.airline;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AirlineResponseWrapper {
    @JsonProperty("response")
    private ApiResponse response;

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
        private List<AirlineInfoResponse> item;
    }
}
