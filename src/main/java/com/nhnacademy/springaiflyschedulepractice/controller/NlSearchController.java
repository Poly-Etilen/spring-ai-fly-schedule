package com.nhnacademy.springaiflyschedulepractice.controller;


import com.nhnacademy.springaiflyschedulepractice.dto.AiFlightSearchResult;
import com.nhnacademy.springaiflyschedulepractice.dto.NlSearchRequest;
import com.nhnacademy.springaiflyschedulepractice.service.CoordinatorSearchService;
import com.nhnacademy.springaiflyschedulepractice.service.NLOrchestrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api/nl-search")
@RequiredArgsConstructor
public class NlSearchController {
    private final NLOrchestrationService orchestrationService;
    private final CoordinatorSearchService coordinatorSearchService;
    private final FlightValidator flightValidator;

    @PostMapping("/search")
    public AiFlightSearchResult search(@RequestBody NlSearchRequest request, BindingResult bindingResult) {
        flightValidator.validate(request, bindingResult);
        if (bindingResult.hasErrors()) {
            String errorMessage = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
            return AiFlightSearchResult.error(errorMessage);
        }

        String message = request.getMessage();
        String model = request.getModel();
        String type = request.getType();

        if (type.equalsIgnoreCase("coordinator")) {
            return coordinatorSearchService.executeCoordinatorSearch(message, model);
        } else {
            return orchestrationService.orchestrateFlightSearch(message, model);
        }
    }
}