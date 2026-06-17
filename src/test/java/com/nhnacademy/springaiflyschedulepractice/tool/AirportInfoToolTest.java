package com.nhnacademy.springaiflyschedulepractice.tool;

import com.nhnacademy.springaiflyschedulepractice.dto.airport.AirportInfoResponse;
import com.nhnacademy.springaiflyschedulepractice.service.ApiClientService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AirportInfoToolTest {
    @Mock
    private ApiClientService apiClientService;

    @InjectMocks
    private AirportInfoTool airportInfoTool;

    @Test
    @DisplayName("전체 공항 목록 조회 테스트")
    void getAirportListTest() {
        List<AirportInfoResponse> mockList = List.of(
                new AirportInfoResponse("NAARKJJ", "광주"),
                new AirportInfoResponse("NAARKPC", "제주")
        );
        given(apiClientService.getAirportList()).willReturn(mockList);

        List<AirportInfoResponse> result = airportInfoTool.getAirportList();

        assertEquals(2, result.size());
        assertEquals("광주", result.getFirst().getAirportName());
    }

    @Test
    @DisplayName("공항 이름으로 공항 코드 조회 성공")
    void getAirportInfoSuccessTest() {
        given(apiClientService.getAirportList()).willReturn(List.of(
                new AirportInfoResponse("NAARKJJ", "광주"),
                new AirportInfoResponse("NAARKPC", "제주")
        ));
        String result = airportInfoTool.getAirportInfo("광주");
        assertEquals("NAARKJJ", result);
    }

    @Test
    @DisplayName("존재하지 않는 공항 이름으로 조회 시 대체 문자열 반환")
    void getAirportInfoFailTest() {
        given(apiClientService.getAirportList()).willReturn(List.of(
                new AirportInfoResponse("NAARKJJ", "광주")
        ));
        String result = airportInfoTool.getAirportInfo("우주");
        assertEquals("알 수 없는 공항: 우주", result);
    }
}
