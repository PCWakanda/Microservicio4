package org.example.microservicio4;

import jakarta.annotation.PostConstruct;
import org.example.microservicio4.flujos.FlujoMaestro;
import org.example.microservicio4.flujos.FlujoVertedero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class Microservicio4Application {

    @Autowired
    private FlujoMaestro flujoMaestro;

    @Autowired
    private FlujoVertedero flujoVertedero;

    public static void main(String[] args) {
        SpringApplication.run(Microservicio4Application.class, args);
    }

    @PostConstruct
    public void init() {
        flujoMaestro.iniciarFlujo();
        flujoVertedero.iniciarFlujo();
    }
}