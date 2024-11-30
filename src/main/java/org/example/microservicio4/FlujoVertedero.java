package org.example.microservicio4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class FlujoVertedero {

    private static final Logger logger = LoggerFactory.getLogger(FlujoVertedero.class);
    private final List<Residuo> residuos = new ArrayList<>();
    private final AtomicInteger tickCounter = new AtomicInteger(0);

    public void iniciarFlujo() {
        Flux.interval(Duration.ofSeconds(4))
            .doOnNext(tick -> {
                logger.info("Tick: {}", tick);
                logger.info("Residuos en el vertedero: {}", residuos);
                if (tickCounter.incrementAndGet() >= 10) {
                    vaciarResiduos();
                    tickCounter.set(0);
                }
            })
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe();
    }

    public void moverResiduos(List<Residuo> nuevosResiduos) {
        residuos.addAll(nuevosResiduos);
        logger.info("Movidos {} residuos al vertedero, total residuos: {}", nuevosResiduos.size(), residuos.size());
    }

    private void vaciarResiduos() {
        logger.info("Vaciando {} residuos", residuos.size());
        residuos.clear();
    }
}