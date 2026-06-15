package com.nhnacademy.springaiflyschedulepractice.controller;


import com.nhnacademy.springaiflyschedulepractice.dto.AiFlightSearchResult;
import com.nhnacademy.springaiflyschedulepractice.service.CoordinatorSearchService;
import com.nhnacademy.springaiflyschedulepractice.service.NLOrchestrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/nl-search")
@RequiredArgsConstructor
public class NlSearchController {
    private final NLOrchestrationService orchestrationService;
    private final CoordinatorSearchService coordinatorSearchService;

    @PostMapping("/search")
    public AiFlightSearchResult search(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        String model = request.getOrDefault("model", "gemini");
        String type = request.getOrDefault("type", "orchestrator");

        if (message == null || message.isBlank()) {
            return AiFlightSearchResult.error("메세지를 입력해주세요");
        }
        if (type.equals("coordinator")) {
            return coordinatorSearchService.executeCoordinatorSearch(message, model);
        } else {
            return orchestrationService.orchestrateFlightSearch(message, model);
        }
    }
}