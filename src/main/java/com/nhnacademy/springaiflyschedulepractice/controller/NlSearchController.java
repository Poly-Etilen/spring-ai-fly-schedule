package com.nhnacademy.springaiflyschedulepractice.controller;



import com.nhnacademy.springaiflyschedulepractice.service.NLOrchestrationService;
import com.nhnacademy.springaiflyschedulepractice.service.OrchestrationResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/nl-search")
public class NlSearchController {

    private final NLOrchestrationService orchestrationService;

    public NlSearchController(NLOrchestrationService orchestrationService) {
        this.orchestrationService = orchestrationService;
    }

    @PostMapping("/search")
    public Map<String, Object> search(@RequestBody Map<String, String> request) {
        String message = request.get("message");

        if (message == null || message.isBlank()) {
            return Map.of(
                    "success", false,
                    "message", "메시지를 입력해주세요."
            );
        }

        OrchestrationResult result =
                orchestrationService.orchestrateFlightSearch(message);

        return result.toResponse();
    }
}