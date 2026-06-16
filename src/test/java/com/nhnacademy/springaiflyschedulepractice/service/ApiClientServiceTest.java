package com.nhnacademy.springaiflyschedulepractice.service;

import com.nhnacademy.springaiflyschedulepractice.config.DataGoKrApiProperties;
import com.nhnacademy.springaiflyschedulepractice.dto.ApiResponseWrapper;
import com.nhnacademy.springaiflyschedulepractice.dto.FlightInfoResponse;
import com.nhnacademy.springaiflyschedulepractice.dto.airline.AirlineInfoResponse;
import com.nhnacademy.springaiflyschedulepractice.dto.airline.AirlineResponseWrapper;
import com.nhnacademy.springaiflyschedulepractice.dto.airport.AirportInfoResponse;
import com.nhnacademy.springaiflyschedulepractice.dto.airport.AirportResponseWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ApiClientServiceTest {

    @Mock
    private DataGoKrApiProperties properties;

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private ApiClientService apiClientService;


    @BeforeEach
    void setUp(){
        apiClientService = new ApiClientService(properties, restClient);

        given(properties.getUrl()).willReturn("https://api.test");
        given(properties.getServiceKey()).willReturn("test-service-key");

        given(restClient.get()).willReturn(requestHeadersUriSpec);
        given(requestHeadersUriSpec.uri(any(URI.class))).willReturn(requestHeadersSpec);
        given(requestHeadersSpec.retrieve()).willReturn(responseSpec);
    }
    @Test
    @DisplayName("항공편 조회 테스트")
    void getFlightSchedulesTest(){
        //api가 응답할 응답객체 생성
        ApiResponseWrapper expected = new ApiResponseWrapper();

        ApiResponseWrapper.ApiResponse apiResponse = new ApiResponseWrapper.ApiResponse();

        ApiResponseWrapper.ResponseHeader header = new ApiResponseWrapper.ResponseHeader();
        header.setResultCode("00");
        header.setResultMsg("성공");

        FlightInfoResponse flight = new FlightInfoResponse(
                "테스트 도착 항공코드",
                "테스트 항공편 아이디",
                "테스트 항공사 이름",
                "테스트 출발 시간",
                "테스트 도착 시간",
                "0",
                "테스트 비즈니스석 가격",
                "테스트 출발 항공코드"
        );

        ApiResponseWrapper.Items items = new ApiResponseWrapper.Items();
        items.setItem(List.of(flight));

        ApiResponseWrapper.ResponseBody body = new ApiResponseWrapper.ResponseBody();
        body.setItems(items);
        body.setNumOfRows(0);
        body.setPageNo(0);
        body.setTotalCount(0);

        apiResponse.setHeader(header);
        apiResponse.setBody(body);

        expected.setApiResponse(apiResponse);
        //api가 응답할 응답객체 생성 종료

        when(responseSpec.body(ApiResponseWrapper.class)).thenReturn(expected);

        List<FlightInfoResponse> actual = apiClientService.getFlightSchedule("testDep", "testArr", "testDate");
        assertEquals(expected.getApiResponse().getBody().getItems().getItem(), actual);
    }

    @Test
    @DisplayName("공항 조회 테스트")
    void getAirportsTest(){
        AirportInfoResponse airport = new AirportInfoResponse(
                "테스트 공항 코드",
                "테스트 공항 이름"
        );

        AirportResponseWrapper expected = new AirportResponseWrapper();

        AirportResponseWrapper.ApiResponse apiResponse = new AirportResponseWrapper.ApiResponse();

        AirportResponseWrapper.ResponseBody body = new AirportResponseWrapper.ResponseBody();

        AirportResponseWrapper.Items items = new AirportResponseWrapper.Items();
        items.setItem(List.of(airport));

        body.setItems(items);
        apiResponse.setBody(body);
        expected.setApiResponse(apiResponse);

        when(responseSpec.body(AirportResponseWrapper.class)).thenReturn(expected);
        List<AirportInfoResponse> actual = apiClientService.getAirportList();
        assertEquals(expected.getApiResponse().getBody().getItems().getItem(), actual);
    }

    @Test
    @DisplayName("항공사 조회 테스트")
    void getAirlineTest(){
        AirlineInfoResponse airline = new AirlineInfoResponse(
                "테스트 항공사 코드",
                "테스트 항공사 이름"
        );

        AirlineResponseWrapper expected = new AirlineResponseWrapper();

        AirlineResponseWrapper.ApiResponse apiResponse = new AirlineResponseWrapper.ApiResponse();

        AirlineResponseWrapper.ResponseBody body = new AirlineResponseWrapper.ResponseBody();

        AirlineResponseWrapper.Items items = new AirlineResponseWrapper.Items();
        items.setItem(List.of(airline));

        body.setItems(items);
        apiResponse.setBody(body);
        expected.setResponse(apiResponse);

        when(responseSpec.body(AirlineResponseWrapper.class)).thenReturn(expected);
        List<AirlineInfoResponse> actual = apiClientService.getAirlineList();
        assertEquals(expected.getResponse().getBody().getItems().getItem(), actual);
    }



}
