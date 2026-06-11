package com.nhnacademy.springaiflyschedulepractice.controller;



import com.nhnacademy.springaiflyschedulepractice.dto.OrchestrationResult;
import com.nhnacademy.springaiflyschedulepractice.service.NLOrchestrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/nl-search")
@RequiredArgsConstructor
public class NlSearchController {
    private final NLOrchestrationService orchestrationService;

    @PostMapping("/search")
    public OrchestrationResult search(@RequestBody Map<String, String> request) {
        String message = request.get("message");

        if (message == null || message.isBlank()) {
            return OrchestrationResult.error("메세지를 입력해주세요");
        }

        OrchestrationResult result =
                orchestrationService.orchestrateFlightSearch(message);

        return result;
    }
}