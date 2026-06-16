package com.nhnacademy.springaiflyschedulepractice.service;

import com.nhnacademy.springaiflyschedulepractice.service.util.ParameterNormalizer;
import com.nhnacademy.springaiflyschedulepractice.dto.AiFlightSearchResult;
import com.nhnacademy.springaiflyschedulepractice.dto.FlightSearchParam;
import com.nhnacademy.springaiflyschedulepractice.dto.airline.AirlineGroup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoordinatorSearchService {
    private final LlmAnalysisService llmAnalysisService;
    private final ParameterNormalizer parameterNormalizer;
    private final FlightCoordinator flightCoordinator;

    public AiFlightSearchResult executeCoordinatorSearch(String message, String model) {
        log.info("Coordinator 방식 검색 시작");
        FlightSearchParam params = llmAnalysisService.extractFlightSearchParam(message, model);
        params = parameterNormalizer.normalize(message, params);

        if (!parameterNormalizer.hasText(params.departure()) ||  !parameterNormalizer.hasText(params.arrival())) {
            return AiFlightSearchResult.error("출발 공항과 도착 공항을 명확히 입력해주세요.");
        }

        List<AirlineGroup> resultData = flightCoordinator.executeSearchWorkflow(params);

        log.info("Coordinator 방식 완료");
        return AiFlightSearchResult.success(params, resultData);
    }
}
