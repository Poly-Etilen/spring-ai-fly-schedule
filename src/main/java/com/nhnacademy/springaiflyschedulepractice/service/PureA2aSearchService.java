package com.nhnacademy.springaiflyschedulepractice.service;

import com.nhnacademy.springaiflyschedulepractice.agent.FlightSearchAgent;
import com.nhnacademy.springaiflyschedulepractice.agent.GroupingAgent;
import com.nhnacademy.springaiflyschedulepractice.agent.PriceFilterAgent;
import com.nhnacademy.springaiflyschedulepractice.agent.TimeFilterAgent;
import com.nhnacademy.springaiflyschedulepractice.dto.FlightDetail;
import com.nhnacademy.springaiflyschedulepractice.dto.FlightInfoResponse;
import com.nhnacademy.springaiflyschedulepractice.dto.FlightSearchParam;
import com.nhnacademy.springaiflyschedulepractice.dto.OrchestrationResult;
import com.nhnacademy.springaiflyschedulepractice.dto.airline.AirlineGroup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PureA2aSearchService {
    private final LlmAnalysisService llmAnalysisService;
    private final ParameterNormalizerAgent parameterNormalizerAgent;
    private final FlightSearchAgent flightSearchAgent;

    public OrchestrationResult executeA2aSearch(String message, String model) {
        log.info("순수 A2A 방식 검색 시작");
        FlightSearchParam params = llmAnalysisService.extractFlightSearchParam(message, model);
        params = parameterNormalizerAgent.normalize(message, params);

        if (!parameterNormalizerAgent.hasText(params.departure()) ||  !parameterNormalizerAgent.hasText(params.arrival())) {
            return OrchestrationResult.error("출발 공항과 도착 공항을 명확히 입력해주세요.");
        }

        List<AirlineGroup> resultData = flightSearchAgent.processFullSearchPipeline(params);

        log.info("순수 A2A 방식 완료");
        return OrchestrationResult.success(params, resultData);
    }
}
