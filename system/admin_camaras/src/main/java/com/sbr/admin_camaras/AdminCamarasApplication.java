package com.sbr.admin_camaras;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@PropertySources({
        @PropertySource("classpath:data.properties"),
        @PropertySource("classpath:kafka_topic_data.properties")
})
@EnableJpaRepositories(basePackages = "com.sbr.data.repositories")
@EntityScan("com.sbr.data.entities")
public class AdminCamarasApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminCamarasApplication.class, args);
    }
}

