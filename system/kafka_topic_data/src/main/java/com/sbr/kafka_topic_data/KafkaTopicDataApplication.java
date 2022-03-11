package com.sbr.kafka_topic_data;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.config.TopicBuilder;

import java.time.Duration;
import java.util.HashMap;

@SpringBootApplication
@PropertySource("classpath:kafka_topic_data.properties")
public class KafkaTopicDataApplication {
    @Value("${localizacaoGestores.topic}")
    private String localizacaoGestoresTopicName;

    @Value("${leiturasCamaras.topic}")
    private String leiturasCamarasTopicName;

    @Value("${notificacoes.topic}")
    private String notificacoesTopicName;

    public static void main(String[] args) {
        SpringApplication.run(KafkaTopicDataApplication.class, args);
    }

    @Bean
    public NewTopic localizacaoGestoresTopic(){
        var configProps = new HashMap<String, String>();

        // Determina que o Kafka irá guardar cada mensagem por 1 hora.
        configProps.put(
                TopicConfig.RETENTION_MS_CONFIG,
                String.valueOf(Duration.ofHours(1).toMillis())
        );

        return TopicBuilder.name(localizacaoGestoresTopicName)
                .configs(configProps)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic leiturasCamarasTopic(){
        var configProps = new HashMap<String, String>();

        // Determina que o Kafka irá guardar cada mensagem por 1 hora.
        configProps.put(
                TopicConfig.RETENTION_MS_CONFIG,
                String.valueOf(Duration.ofHours(1).toMillis())
        );

        return TopicBuilder.name(leiturasCamarasTopicName)
                .configs(configProps)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic notificacoesTopic(){
        var configProps = new HashMap<String, String>();

        // Determina que o Kafka irá guardar cada mensagem por 7 dias.
        configProps.put(
                TopicConfig.RETENTION_MS_CONFIG,
                String.valueOf(Duration.ofDays(7).toMillis())
        );

        return TopicBuilder.name(notificacoesTopicName)
                .configs(configProps)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
