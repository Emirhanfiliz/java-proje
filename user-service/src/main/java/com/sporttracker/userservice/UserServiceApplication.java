package com.sporttracker.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import com.sporttracker.shared.security.JwtProperties;

@EnableCaching
@EnableConfigurationProperties(JwtProperties.class)
@ComponentScan(basePackages = {"com.sporttracker.userservice", "com.sporttracker.shared"})
@SpringBootApplication
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

}