package com.sbr.kafka_topic_data.utils;

import org.springframework.kafka.support.serializer.JsonSerializer;

public class KafkaEntitySerializer<T> extends JsonSerializer<T> {}
