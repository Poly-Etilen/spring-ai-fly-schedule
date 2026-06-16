package com.nhnacademy.springaiflyschedulepractice.controller;


import com.nhnacademy.springaiflyschedulepractice.dto.NlSearchRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class FlightValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return NlSearchRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        NlSearchRequest request = (NlSearchRequest) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "message", "Field required", "메세지를 입력해주세요");
        if (request.getModel() != null && !(request.getModel().equalsIgnoreCase("gemini") || request.getModel().equals("ollama"))) {
            errors.rejectValue("model", "field invalid", "지원하지 않는 모델입니다.");
        }
    }
}
