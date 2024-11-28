package org.example.microservicio4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class Microservicio4Application {

    public static void main(String[] args) {
        SpringApplication.run(Microservicio4Application.class, args);
    }
}