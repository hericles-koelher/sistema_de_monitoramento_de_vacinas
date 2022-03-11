package com.sbr.web_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
// Essas anotações aqui são essenciais para que as coisas funcionem corretamente.
// Sem elas os repositories, entidades e configurações de acesso não são identificadas  corretamente.
@PropertySources({
        @PropertySource("classpath:data.properties"),
        @PropertySource("classpath:kafka_topic_data.properties")
})
@EnableJpaRepositories(basePackages = "com.sbr.data.repositories")
@EntityScan("com.sbr.data.entities")
public class WebServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebServiceApplication.class, args);
    }

}
