package com.nhnacademy.springaiflyschedulepractice.tool;

import com.nhnacademy.springaiflyschedulepractice.dto.airline.AirlineInfoResponse;
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
class AirlineInfoToolTest {
    @Mock
    private ApiClientService apiClientService;

    @InjectMocks
    private AirlineInfoTool airlineInfoTool;

    @Test
    @DisplayName("전체 항공사 목록 조회 테스트")
    void getAirlineListTest() {
        List<AirlineInfoResponse> mockList = List.of(
                new AirlineInfoResponse("AAR", "아시아나항공"),
                new AirlineInfoResponse("KAR", "대한항공")
        );
        given(apiClientService.getAirlineList()).willReturn(mockList);
        List<AirlineInfoResponse> result = airlineInfoTool.getAirlineList();
        assertEquals(2, result.size());
        assertEquals("아시아나항공", result.getFirst().getAirlineNm());
    }

    @Test
    @DisplayName("항공사 이음으로 ID 조회 성공")
    void getAirlineIdSuccessTest() {
        given(apiClientService.getAirlineList()).willReturn(List.of(
                new AirlineInfoResponse("AAR", "아시아나항공"),
                new AirlineInfoResponse("KAR", "대한항공")
                ));
        String result = airlineInfoTool.getAirlineId("대한항공");
        assertEquals("KAR", result);
    }

    @Test
    @DisplayName("존재하지 않는 항공사 이름으로 조회 시 대체 문제열 반환")
    void getAirlineIdFailTest() {
        given(apiClientService.getAirlineList()).willReturn(List.of(
                new AirlineInfoResponse("AAR", "아시아나항공")
        ));
        String result = airlineInfoTool.getAirlineId("우주항공");
        assertEquals("알 수 없는 항공사: 우주항공", result);
    }
}
