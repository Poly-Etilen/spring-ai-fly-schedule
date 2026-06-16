package com.nhnacademy.springaiflyschedulepractice.service;

import com.nhnacademy.springaiflyschedulepractice.dto.AiFlightSearchResult;
import com.nhnacademy.springaiflyschedulepractice.dto.FlightDetail;
import com.nhnacademy.springaiflyschedulepractice.dto.FlightSearchParam;
import com.nhnacademy.springaiflyschedulepractice.dto.airline.AirlineGroup;
import com.nhnacademy.springaiflyschedulepractice.service.util.ParameterNormalizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


import static org.mockito.Mockito.*;


class CoordinatorSearchServiceTest {
    private LlmAnalysisService llmAnalysisService;
    private ParameterNormalizer parameterNormalizer;
    private FlightCoordinator flightCoordinator;
    private CoordinatorSearchService coordinatorSearchService;

    @BeforeEach
    void setUp() {
        llmAnalysisService = mock(LlmAnalysisService.class);
        parameterNormalizer = new ParameterNormalizer();
        flightCoordinator = mock(FlightCoordinator.class);
        coordinatorSearchService = new CoordinatorSearchService(
                llmAnalysisService,
                parameterNormalizer,
                flightCoordinator
        );
    }

    @Test
    @DisplayName("메서드 호출 검사 및 반환값 검사")
    void executeCoordinatorSearchTest(){
        String message = "내일 광주에서 제주로 가는 항공편을 알려줘";
        String model = "ollama";

        FlightSearchParam expectedParams = new FlightSearchParam(
                "광주",
                "제주",
                LocalDate.now().plusDays(1).toString(),
                null,
                null,
                null,
                null
        );

        List<AirlineGroup> expectedData = List.of(
                new AirlineGroup("테스트 항공편명",
                        List.of(new FlightDetail(
                                "테스트 항공아이디",
                                "테스트 항공편명",
                                "테스트 출발시간",
                                "테스트 도착시간",
                                0)))
        );


        // paramterNormalizer는 llm이 검증된 파라미터를 제시한다는 가정으로 제외함.
        when(llmAnalysisService.extractFlightSearchParam(message, model)).thenReturn(expectedParams);
        when(flightCoordinator.executeSearchWorkflow(expectedParams)).thenReturn(expectedData);


        AiFlightSearchResult actual = coordinatorSearchService.executeCoordinatorSearch(message, model);

        //메서드 호출 검사(유틸은 제외함)
        assertAll(
                () -> verify(llmAnalysisService, times(1)).extractFlightSearchParam(anyString(), anyString()),
                () -> verify(flightCoordinator, times(1)).executeSearchWorkflow(any(FlightSearchParam.class))
        );

        //성공 응답 값 검증
        assertAll(
                () -> assertTrue(actual.success()),
                () -> assertEquals("성공", actual.message()),
                () -> assertEquals(expectedData, actual.data()),
                () -> assertEquals(expectedParams, actual.param())

        );
    }


    @Test
    @DisplayName("에러응답 반환검사")
    void errorResultTest(){
        String message = "잘못된 메시지 요청";
        String model = "ollama";
        FlightSearchParam wrongParam = new FlightSearchParam(
                "광주",
                null,
                LocalDate.now().plusDays(1).toString(),
                null,
                null,
                null,
                null
        );

        when(llmAnalysisService.extractFlightSearchParam(message, model)).thenReturn(wrongParam);

        AiFlightSearchResult actual = coordinatorSearchService.executeCoordinatorSearch(message, model);

        //최종 메서드 호출 검사 - 에러응답을 중간에 리턴하므로 실행 x
        assertAll(
                () -> verify(flightCoordinator, never()).executeSearchWorkflow(any())
        );

        //에러 응답 값 검증
        assertAll(
                () -> assertFalse(actual.success()),
                () -> assertEquals("출발 공항과 도착 공항을 명확히 입력해주세요.", actual.message()),
                () -> assertNull(actual.data()),
                () -> assertNull(actual.param())
        );
    }
}
