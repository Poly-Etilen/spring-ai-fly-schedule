package com.nhnacademy.springaiflyschedulepractice.service;

import com.nhnacademy.springaiflyschedulepractice.agent.FlightSearchAgent;
import com.nhnacademy.springaiflyschedulepractice.dto.FlightSearchParam;
import com.nhnacademy.springaiflyschedulepractice.dto.OrchestrationResult;
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
    private final ParameterNormalizerAgent parameterNormalizerAgent;
    private final FlightCoordinator flightCoordinator;

    public OrchestrationResult executeCoordinatorSearch(String message, String model) {
        log.info("Coordinator 방식 검색 시작");
        FlightSearchParam params = llmAnalysisService.extractFlightSearchParam(message, model);
        params = parameterNormalizerAgent.normalize(message, params);

        if (!parameterNormalizerAgent.hasText(params.departure()) ||  !parameterNormalizerAgent.hasText(params.arrival())) {
            return OrchestrationResult.error("출발 공항과 도착 공항을 명확히 입력해주세요.");
        }

        List<AirlineGroup> resultData = flightCoordinator.executeSearchWorkflow(params);

        log.info("Coordinator 방식 완료");
        return OrchestrationResult.success(params, resultData);
    }
}
