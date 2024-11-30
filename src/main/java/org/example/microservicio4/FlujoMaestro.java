package org.example.microservicio4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
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
    private final List<Residuo> residuosRuta1 = new ArrayList<>();
    private final List<Residuo> residuosRuta2 = new ArrayList<>();
    private final List<Residuo> residuosRuta3 = new ArrayList<>();

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void iniciarFlujo() {
        Flux.interval(Duration.ofSeconds(4))
            .doOnNext(tick -> {
                logger.info("Tick Maestro: {}", tick);
                rutaDeBasura1();
                rutaDeBasura2();
                rutaDeBasura3();
            })
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe();
    }

    private void rutaDeBasura1() {
        int numeroResiduos = random.nextInt(5) + 1;
        generarResiduos(numeroResiduos, contadorRuta1, residuosRuta1);
        int sumaTotal = contadorRuta1.get();
        logger.info("Ruta de Basura 1 - Residuos: {}, Suma Total: {}", residuosRuta1, sumaTotal);
        if (sumaTotal >= 30) {
            rabbitTemplate.convertAndSend("residuosQueue", "ruta1");
        }
    }

    private void rutaDeBasura2() {
        int numeroResiduos = random.nextInt(5) + 1;
        generarResiduos(numeroResiduos, contadorRuta2, residuosRuta2);
        int sumaTotal = contadorRuta2.get();
        logger.info("Ruta de Basura 2 - Residuos: {}, Suma Total: {}", residuosRuta2, sumaTotal);
        if (sumaTotal >= 30) {
            rabbitTemplate.convertAndSend("residuosQueue", "ruta2");
        }
    }

    private void rutaDeBasura3() {
        int numeroResiduos = random.nextInt(5) + 1;
        generarResiduos(numeroResiduos, contadorRuta3, residuosRuta3);
        int sumaTotal = contadorRuta3.get();
        logger.info("Ruta de Basura 3 - Residuos: {}, Suma Total: {}", residuosRuta3, sumaTotal);
        if (sumaTotal >= 30) {
            rabbitTemplate.convertAndSend("residuosQueue", "ruta3");
        }
    }

    private void generarResiduos(int numeroResiduos, AtomicInteger contador, List<Residuo> residuos) {
        IntStream.range(0, numeroResiduos)
                .mapToObj(i -> new Residuo("residuo" + contador.getAndIncrement()))
                .forEach(residuos::add);
    }

    public List<Residuo> getResiduosRuta1() {
        return residuosRuta1;
    }

    public List<Residuo> getResiduosRuta2() {
        return residuosRuta2;
    }

    public List<Residuo> getResiduosRuta3() {
        return residuosRuta3;
    }

    public AtomicInteger getContadorRuta1() {
        return contadorRuta1;
    }

    public AtomicInteger getContadorRuta2() {
        return contadorRuta2;
    }

    public AtomicInteger getContadorRuta3() {
        return contadorRuta3;
    }

    public void clearResiduosRuta1() {
        residuosRuta1.clear();
    }

    public void clearResiduosRuta2() {
        residuosRuta2.clear();
    }

    public void clearResiduosRuta3() {
        residuosRuta3.clear();
    }
}