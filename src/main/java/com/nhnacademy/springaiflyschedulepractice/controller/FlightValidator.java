package com.nhnacademy.springaiflyschedulepractice.controller;


import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class FlightValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(NlSearchController.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

    }
}
