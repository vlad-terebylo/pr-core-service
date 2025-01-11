package com.tvo.propertyregister;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PropertyRegisterApplication {

    public static void main(String[] args) {
        SpringApplication.run(PropertyRegisterApplication.class, args);
    }

}
