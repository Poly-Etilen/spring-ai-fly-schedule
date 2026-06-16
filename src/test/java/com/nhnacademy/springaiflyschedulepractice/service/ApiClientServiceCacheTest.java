package com.nhnacademy.springaiflyschedulepractice.service;

import com.nhnacademy.springaiflyschedulepractice.dto.airport.AirportInfoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verifyNoInteractions;

@SpringBootTest
class ApiClientServiceCacheTest {

    @Autowired
    private ApiClientService apiClientService;

    @Autowired
    private CacheManager cacheManager;

    @MockitoBean
    private RestClient restClient;

    @BeforeEach
    void setUp() {
        cacheManager.getCache("airports").clear();
    }

    @Test
    @DisplayName("공항 캐시 히트 시 API 요청을 실행하지 않는다")
    void airportCacheHitTest() {
        List<AirportInfoResponse> expected = List.of(
                new AirportInfoResponse("KWJ", "광주")
        );

        cacheManager.getCache("airports").put(SimpleKey.EMPTY, expected);

        List<AirportInfoResponse> actual = apiClientService.getAirportList();

        assertEquals(expected, actual);
        verifyNoInteractions(restClient); // restClient가 실행이 안되는지 검증 -> 캐시히트면 메서드를 실행하지않고 값을 가져오기때문에
    }
}
