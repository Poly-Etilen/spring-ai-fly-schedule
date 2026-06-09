package com.nhnacademy.springaiflyschedulepractice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class SpringAiFlySchedulePracticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiFlySchedulePracticeApplication.class, args);
    }

}
