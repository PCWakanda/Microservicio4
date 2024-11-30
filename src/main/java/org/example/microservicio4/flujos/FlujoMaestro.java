package org.example.microservicio4.flujos;

import io.micrometer.core.instrument.MeterRegistry;
import org.example.microservicio4.residuos.RutaBasura1;
import org.example.microservicio4.residuos.RutaBasura2;
import org.example.microservicio4.residuos.RutaBasura3;
import org.example.microservicio4.residuos.Residuo;
import org.example.microservicio4.repository.RutaBasura1Repository;
import org.example.microservicio4.repository.RutaBasura2Repository;
import org.example.microservicio4.repository.RutaBasura3Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
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

    @Autowired
    private RutaBasura1Repository rutaBasura1Repository;

    @Autowired
    private RutaBasura2Repository rutaBasura2Repository;

    @Autowired
    private RutaBasura3Repository rutaBasura3Repository;

    @Autowired
    public FlujoMaestro(MeterRegistry meterRegistry) {
        meterRegistry.gauge("residuos.ruta1.size", residuosRuta1, List::size);
        meterRegistry.gauge("residuos.ruta2.size", residuosRuta2, List::size);
        meterRegistry.gauge("residuos.ruta3.size", residuosRuta3, List::size);
    }

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

    @Transactional
    protected void rutaDeBasura1() {
        int numeroResiduos = random.nextInt(5) + 1;
        List<RutaBasura1> nuevosResiduos = generarResiduosRuta1(numeroResiduos, contadorRuta1);
        rutaBasura1Repository.saveAll(nuevosResiduos);
        residuosRuta1.addAll(nuevosResiduos);
        int sumaTotal = contadorRuta1.get();
        logger.info("Ruta de Basura 1 - Residuos: {}, Suma Total: {}", residuosRuta1, sumaTotal);
        if (sumaTotal >= 30) {
            rabbitTemplate.convertAndSend("residuosQueue", "ruta1");
        }
    }

    @Transactional
    protected void rutaDeBasura2() {
        int numeroResiduos = random.nextInt(5) + 1;
        List<RutaBasura2> nuevosResiduos = generarResiduosRuta2(numeroResiduos, contadorRuta2);
        rutaBasura2Repository.saveAll(nuevosResiduos);
        residuosRuta2.addAll(nuevosResiduos);
        int sumaTotal = contadorRuta2.get();
        logger.info("Ruta de Basura 2 - Residuos: {}, Suma Total: {}", residuosRuta2, sumaTotal);
        if (sumaTotal >= 30) {
            rabbitTemplate.convertAndSend("residuosQueue", "ruta2");
        }
    }

    @Transactional
    protected void rutaDeBasura3() {
        int numeroResiduos = random.nextInt(5) + 1;
        List<RutaBasura3> nuevosResiduos = generarResiduosRuta3(numeroResiduos, contadorRuta3);
        rutaBasura3Repository.saveAll(nuevosResiduos);
        residuosRuta3.addAll(nuevosResiduos);
        int sumaTotal = contadorRuta3.get();
        logger.info("Ruta de Basura 3 - Residuos: {}, Suma Total: {}", residuosRuta3, sumaTotal);
        if (sumaTotal >= 30) {
            rabbitTemplate.convertAndSend("residuosQueue", "ruta3");
        }
    }

    @Transactional
    public void clearResiduosRuta1() {
        residuosRuta1.clear();
        rutaBasura1Repository.deleteAll();
    }

    @Transactional
    public void clearResiduosRuta2() {
        residuosRuta2.clear();
        rutaBasura2Repository.deleteAll();
    }

    @Transactional
    public void clearResiduosRuta3() {
        residuosRuta3.clear();
        rutaBasura3Repository.deleteAll();
    }

    private List<RutaBasura1> generarResiduosRuta1(int numeroResiduos, AtomicInteger contador) {
        return IntStream.range(0, numeroResiduos)
                .mapToObj(i -> new RutaBasura1("residuo" + contador.getAndIncrement()))
                .collect(Collectors.toList());
    }

    private List<RutaBasura2> generarResiduosRuta2(int numeroResiduos, AtomicInteger contador) {
        return IntStream.range(0, numeroResiduos)
                .mapToObj(i -> new RutaBasura2("residuo" + contador.getAndIncrement()))
                .collect(Collectors.toList());
    }

    private List<RutaBasura3> generarResiduosRuta3(int numeroResiduos, AtomicInteger contador) {
        return IntStream.range(0, numeroResiduos)
                .mapToObj(i -> new RutaBasura3("residuo" + contador.getAndIncrement()))
                .collect(Collectors.toList());
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
}