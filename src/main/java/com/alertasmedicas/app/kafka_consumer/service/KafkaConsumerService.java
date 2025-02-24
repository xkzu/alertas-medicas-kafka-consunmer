package com.alertasmedicas.app.kafka_consumer.service;

import com.alertasmedicas.app.kafka_consumer.dto.MeasurementDTO;
import com.alertasmedicas.app.kafka_consumer.util.MessageParser;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class KafkaConsumerService {

    private final RestTemplate restTemplate;

    @Value("${api.measurement:}")
    private String domain;

    private final List<MeasurementDTO> measurements = new ArrayList<>();

    @Autowired
    public KafkaConsumerService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @KafkaListener(topics = "#{@topic}", groupId = "#{@groupId}")
    public void consume(String message) {
        log.info("Mensaje consumido: {}", message);
        saveAnomaly(message);
    }

    public List<MeasurementDTO> getMeasurementsInQueue() {
        List<MeasurementDTO> responseMeasurements = new ArrayList<>(measurements);
        measurements.clear();
        return responseMeasurements;
    }

    private void saveAnomaly(String message) {
        log.info("Guardando anomalia en bd: {}", message);
        MeasurementDTO measurementDTO = MessageParser.parseMeasurement(message);
        log.info("Parseando message a measurement: {}", measurementDTO);
        MeasurementDTO measurementSaved = saveMeasurement(measurementDTO);
        log.info("Anomalia guardada: {}", measurementSaved);
        measurements.add(measurementSaved);
    }

    private MeasurementDTO saveMeasurement(MeasurementDTO measurementDTO) {
        String url = domain + "/measurement/add";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MeasurementDTO> requestEntity = new HttpEntity<>(measurementDTO, headers);
        ResponseEntity<MeasurementDTO> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                MeasurementDTO.class
        );
        return response.getBody();
    }
}
