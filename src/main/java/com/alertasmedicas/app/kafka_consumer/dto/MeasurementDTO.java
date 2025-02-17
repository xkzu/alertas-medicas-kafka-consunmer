package com.alertasmedicas.app.kafka_consumer.dto;

import java.time.LocalDateTime;

public record MeasurementDTO(
        Long id,
        Long idPatient,
        Long idSing,
        double measurementValue,
        LocalDateTime dateTime
) {}
