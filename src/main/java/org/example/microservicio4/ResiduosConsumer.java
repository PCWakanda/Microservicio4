package org.example.microservicio4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ResiduosConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ResiduosConsumer.class);

    @Autowired
    private FlujoVertedero flujoVertedero;

    @Autowired
    private FlujoMaestro flujoMaestro;

    @RabbitListener(queues = "residuosQueue")
    public void handleResiduos(String ruta) {
        List<Residuo> residuos;
        AtomicInteger contador;
        switch (ruta) {
            case "ruta1":
                residuos = new ArrayList<>(flujoMaestro.getResiduosRuta1());
                contador = flujoMaestro.getContadorRuta1();
                flujoMaestro.clearResiduosRuta1();
                break;
            case "ruta2":
                residuos = new ArrayList<>(flujoMaestro.getResiduosRuta2());
                contador = flujoMaestro.getContadorRuta2();
                flujoMaestro.clearResiduosRuta2();
                break;
            case "ruta3":
                residuos = new ArrayList<>(flujoMaestro.getResiduosRuta3());
                contador = flujoMaestro.getContadorRuta3();
                flujoMaestro.clearResiduosRuta3();
                break;
            default:
                logger.error("Ruta desconocida: {}", ruta);
                return;
        }
        flujoVertedero.moverResiduos(residuos);
        contador.set(0);
        logger.info("Residuos de {} movidos al vertedero", ruta);
    }
}