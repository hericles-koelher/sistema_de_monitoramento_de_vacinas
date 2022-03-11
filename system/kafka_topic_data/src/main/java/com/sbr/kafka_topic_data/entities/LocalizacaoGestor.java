package com.sbr.kafka_topic_data.entities;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocalizacaoGestor {
    private double latitude;
    private double longitude;
}
