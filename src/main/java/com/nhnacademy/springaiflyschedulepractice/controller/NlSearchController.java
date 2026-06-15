package com.nhnacademy.springaiflyschedulepractice.controller;


import com.nhnacademy.springaiflyschedulepractice.dto.OrchestrationResult;
import com.nhnacademy.springaiflyschedulepractice.service.NLOrchestrationService;
import com.nhnacademy.springaiflyschedulepractice.service.CoordinatorSearchService;
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
    private final CoordinatorSearchService pureA2aSearchService;

    @PostMapping("/search")
    public OrchestrationResult search(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        String model = request.getOrDefault("model", "gemini");
        String type = request.getOrDefault("type", "orchestrator");

        if (message == null || message.isBlank()) {
            return OrchestrationResult.error("메세지를 입력해주세요");
        }
        if (type.equals("a2a")) {
            return pureA2aSearchService.executeA2aSearch(message, model);
        } else {
            return orchestrationService.orchestrateFlightSearch(message, model);
        }
    }
}