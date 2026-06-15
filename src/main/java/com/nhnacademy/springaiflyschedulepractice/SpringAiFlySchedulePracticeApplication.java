package com.nhnacademy.springaiflyschedulepractice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesSource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
//@EnableConfigurationProperties
//@ConfigurationPropertiesScan
public class SpringAiFlySchedulePracticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiFlySchedulePracticeApplication.class, args);
    }

}
