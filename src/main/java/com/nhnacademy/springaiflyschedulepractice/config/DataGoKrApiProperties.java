package com.nhnacademy.springaiflyschedulepractice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "data-go-kr.api")
public class DataGoKrApiProperties {
    private String url;
    private String serviceKey;
}
