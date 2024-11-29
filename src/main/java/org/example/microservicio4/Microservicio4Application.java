package org.example.microservicio4;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class Microservicio4Application {

    private static final Logger logger = LoggerFactory.getLogger(Microservicio4Application.class);

    @Autowired
    private LogService logService;

    public static void main(String[] args) {
        SpringApplication.run(Microservicio4Application.class, args);
    }

    @PostConstruct
    public void logApplicationStart() {
        logger.info("Application started successfully.");
    }
}