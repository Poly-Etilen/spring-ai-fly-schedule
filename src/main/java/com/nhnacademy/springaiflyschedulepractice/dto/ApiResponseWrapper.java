package com.nhnacademy.springaiflyschedulepractice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ApiResponseWrapper {
    @JsonProperty("response")
    private ApiResponse apiResponse;

    @Data
    public static class ApiResponse {
        @JsonProperty("header")
        private ResponseHeader header;

        @JsonProperty("body")
        private ResponseBody body;
    }

    @Data
    public static class ResponseHeader {
        @JsonProperty("resultCode")
        private String resultCode;

        @JsonProperty("resultMsg")
        private String resultMsg;
    }

    @Data
    public static class ResponseBody {
        @JsonProperty("items")
        private Items items;

        @JsonProperty("numOfRows")
        private Integer numOfRows;

        @JsonProperty("pageNo")
        private Integer pageNo;

        @JsonProperty("totalCount")
        private Integer totalCount;
    }

    @Data
    public static class Items {
        @JsonProperty("item")
        private List<FlightInfoResponse> item;
    }
}
