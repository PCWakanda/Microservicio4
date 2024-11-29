package org.example.microservicio4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Service
public class FlujoMaestro {

    private static final Logger logger = LoggerFactory.getLogger(FlujoMaestro.class);
    private final Random random = new Random();
    private final AtomicInteger contadorRuta1 = new AtomicInteger(0);
    private final AtomicInteger contadorRuta2 = new AtomicInteger(0);
    private final AtomicInteger contadorRuta3 = new AtomicInteger(0);

    public void iniciarFlujo() {
        Flux.interval(Duration.ofSeconds(4))
            .doOnNext(tick -> {
                logger.info("Tick: {}", tick);
                rutaDeBasura1();
                rutaDeBasura2();
                rutaDeBasura3();
            })
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe();
    }

    private void rutaDeBasura1() {
        int numeroResiduos = random.nextInt(5) + 1;
        Residuo[] residuos = generarResiduos(numeroResiduos, contadorRuta1);
        int sumaTotal = contadorRuta1.get();
        logger.info("Ruta de Basura 1 - Residuos: {}, Suma Total: {}", (Object) residuos, sumaTotal);
    }

    private void rutaDeBasura2() {
        int numeroResiduos = random.nextInt(5) + 1;
        Residuo[] residuos = generarResiduos(numeroResiduos, contadorRuta2);
        int sumaTotal = contadorRuta2.get();
        logger.info("Ruta de Basura 2 - Residuos: {}, Suma Total: {}", (Object) residuos, sumaTotal);
    }

    private void rutaDeBasura3() {
        int numeroResiduos = random.nextInt(5) + 1;
        Residuo[] residuos = generarResiduos(numeroResiduos, contadorRuta3);
        int sumaTotal = contadorRuta3.get();
        logger.info("Ruta de Basura 3 - Residuos: {}, Suma Total: {}", (Object) residuos, sumaTotal);
    }

    private Residuo[] generarResiduos(int numeroResiduos, AtomicInteger contador) {
        return IntStream.range(0, numeroResiduos)
                .mapToObj(i -> new Residuo("residuo" + contador.getAndIncrement()))
                .toArray(Residuo[]::new);
    }
}