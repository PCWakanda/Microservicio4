package org.example.microservicio4.flujos;

import io.micrometer.core.instrument.MeterRegistry;
import org.example.microservicio4.residuos.Residuo;
import org.example.microservicio4.residuos.Vertedero;
import org.example.microservicio4.repository.VertederoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class FlujoVertedero {

    private static final Logger logger = LoggerFactory.getLogger(FlujoVertedero.class);
    private final List<Residuo> residuos = new ArrayList<>();
    private final AtomicInteger tickCounter = new AtomicInteger(0);

    @Autowired
    private VertederoRepository vertederoRepository;

    public FlujoVertedero(MeterRegistry meterRegistry) {
        meterRegistry.gauge("residuos.vertedero.size", residuos, List::size);
    }

    public void iniciarFlujo() {
        Flux.interval(Duration.ofSeconds(2))
            .doOnNext(tick -> {
                logger.info("Tick Vertedero: {}", tick);
                logger.info("Residuos en el vertedero: {}, Total: {}", residuos, residuos.size());
                if (tickCounter.incrementAndGet() >= 10) {
                    vaciarResiduos();
                    tickCounter.set(0);
                }
            })
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe();
    }

    @Transactional
    public void moverResiduos(List<Residuo> nuevosResiduos) {
        List<Vertedero> nuevosVertederos = nuevosResiduos.stream()
                .map(residuo -> new Vertedero(residuo.getNombre()))
                .collect(Collectors.toList());
        residuos.addAll(nuevosResiduos);
        vertederoRepository.saveAll(nuevosVertederos);
        logger.info("Movidos {} residuos al vertedero, total residuos: {}", nuevosResiduos.size(), residuos.size());
    }

    @Transactional
    private void vaciarResiduos() {
        logger.info("Vaciando {} residuos", residuos.size());
        residuos.clear();
        vertederoRepository.deleteAll();
    }
}