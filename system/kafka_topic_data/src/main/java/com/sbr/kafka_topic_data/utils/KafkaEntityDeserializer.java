package com.sbr.kafka_topic_data.utils;

import org.springframework.kafka.support.serializer.JsonDeserializer;

public class KafkaEntityDeserializer<T> extends JsonDeserializer<T> {
    public KafkaEntityDeserializer(){
        super();
        this.addTrustedPackages("com.sbr.kafka_topic_data.entities");
    }
}
