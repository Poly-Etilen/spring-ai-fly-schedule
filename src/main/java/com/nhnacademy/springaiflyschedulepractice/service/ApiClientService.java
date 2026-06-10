package com.nhnacademy.springaiflyschedulepractice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.springaiflyschedulepractice.config.DataGoKrApiProperties;
import com.nhnacademy.springaiflyschedulepractice.dto.*;
import com.nhnacademy.springaiflyschedulepractice.dto.airline.AirlineInfoResponse;
import com.nhnacademy.springaiflyschedulepractice.dto.airline.AirlineResponseWrapper;
import com.nhnacademy.springaiflyschedulepractice.dto.airport.AirportInfoResponse;
import com.nhnacademy.springaiflyschedulepractice.dto.airport.AirportResponseWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApiClientService {
    private final DataGoKrApiProperties apiProperties;
    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Cacheable(value = "flights", key = "#depAirportId + '-' + #arrAirportId + '-' + #date")
    public List<FlightInfoResponse> getFlightSchedule(String depAirportId, String arrAirportId, String date) {
        try {
            String url = UriComponentsBuilder.fromUriString(apiProperties.getUrl() + "/GetFlightOpratInfoList")
                    .queryParam("serviceKey", apiProperties.getServiceKey())
                    .queryParam("depAirportId", depAirportId)
                    .queryParam("arrAirportId", arrAirportId)
                    .queryParam("depPlandTime", date)
                    .queryParam("_type", "json")
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUriString();
            log.info("URL: {}", url);

            ApiResponseWrapper response = restClient.get()
                    .uri(URI.create(url))
                    .retrieve()
                    .body(ApiResponseWrapper.class);

            if (response != null && response.getApiResponse() != null) {
                String resultCode = response.getApiResponse().getHeader().getResultCode();

                if (resultCode.equals("00")) {
                    List<FlightInfoResponse> items = response.getApiResponse().getBody().getItems().getItem();
                    log.info("항공편 {}건 조회 완료", items.size());
                    return items;
                } else {
                    log.error("API 에러: {} - {}", resultCode, response.getApiResponse().getHeader().getResultMsg());
                }
            }
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("API 호출 실패", e);
            return Collections.emptyList();
        }
    }

    @Cacheable("airports")
    public List<AirportInfoResponse> getAirportList() {
        try {
            String url = UriComponentsBuilder.fromUriString(apiProperties.getUrl() + "/GetArprtList")
                    .queryParam("serviceKey", apiProperties.getServiceKey())
                    .queryParam("_type", "json")
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUriString();
            log.info("URL: {}", url);

            AirportResponseWrapper response = restClient.get()
                    .uri(URI.create(url))
                    .retrieve()
                    .body(AirportResponseWrapper.class);

            if (response != null && response.getApiResponse() != null && response.getApiResponse().getBody() != null) {
                List<AirportInfoResponse> items = response.getApiResponse().getBody().getItems().getItem();
                log.info("공항 {}건 조회 완료", items.size());
                return items;
            }
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("공항 목록 조회 실패", e);
            return Collections.emptyList();
        }
    }

    @Cacheable("airlines")
    public List<AirlineInfoResponse> getAirlineList() {
        try {
            String url = UriComponentsBuilder.fromUriString(apiProperties.getUrl() + "/GetAirmanList")
                    .queryParam("serviceKey", apiProperties.getServiceKey())
                    .queryParam("_type", "json")
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUriString();
            log.info("URL: {}", url);
            AirlineResponseWrapper response = restClient.get()
                    .uri(URI.create(url))
                    .retrieve()
                    .body(AirlineResponseWrapper.class);

            if (response != null && response.getResponse() != null && response.getResponse().getBody() != null) {
                List<AirlineInfoResponse> items = response.getResponse().getBody().getItems().getItem();
                log.info("항공사 {}건 조회 완료", items.size());
                return items;
            }
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("항공사 목록 조회 실패", e);
            return Collections.emptyList();
        }
    }
}
