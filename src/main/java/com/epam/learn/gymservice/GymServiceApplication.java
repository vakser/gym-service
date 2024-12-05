package com.epam.learn.gymservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class GymServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GymServiceApplication.class, args);
    }

}
