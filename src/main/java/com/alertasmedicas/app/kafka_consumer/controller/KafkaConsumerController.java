package com.alertasmedicas.app.kafka_consumer.controller;

import com.alertasmedicas.app.kafka_consumer.dto.MeasurementDTO;
import com.alertasmedicas.app.kafka_consumer.service.KafkaConsumerService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/kafka")
public class KafkaConsumerController {

    private final KafkaConsumerService kafkaConsumerService;

    @Autowired
    public KafkaConsumerController(KafkaConsumerService kafkaConsumerService) {
        this.kafkaConsumerService = kafkaConsumerService;
    }

    @GetMapping("/measurements")
    public ResponseEntity<List<MeasurementDTO>> getMeasurements() {
        try {
            log.info("Obteniendo mediciones desde la cola");
            return ResponseEntity.ok(kafkaConsumerService.getMeasurementsInQueue());
        } catch (Exception e) {
            log.error("Error al obtener mediciones desde la cola {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }

    }
}
