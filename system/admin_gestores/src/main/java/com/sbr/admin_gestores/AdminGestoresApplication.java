package com.sbr.admin_gestores;

import com.sbr.data.entities.Gestor;
import com.sbr.data.repositories.GestorRepository;
import com.sbr.kafka_topic_data.entities.LocalizacaoGestor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.Optional;

@SpringBootApplication
// Essas anotações aqui são essenciais para que as coisas funcionem corretamente.
// Sem elas os repositories, entidades e configurações de acesso não são identificadas  corretamente.
@PropertySources({
        @PropertySource("classpath:data.properties"),
        @PropertySource("classpath:kafka_topic_data.properties")
})
@EnableJpaRepositories(basePackages = "com.sbr.data.repositories")
@EntityScan("com.sbr.data.entities")
@Slf4j
@RequiredArgsConstructor
public class AdminGestoresApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminGestoresApplication.class, args);
    }

    private final GestorRepository gestorRepository;

    @KafkaListener(id = "admin_gestores", topics = "${localizacaoGestores.topic}")
    public void listen(ConsumerRecord<Long, LocalizacaoGestor> record) {
        log.info("Atualizando a localização de um gestor.");
        LocalizacaoGestor novaLocalizacaoGestor = record.value();

        Optional<Gestor> optionalGestor = gestorRepository
                .findById(record.key());

        optionalGestor.ifPresentOrElse((Gestor g) -> {
          g.setLatitude(novaLocalizacaoGestor.getLatitude());
          g.setLongitude(novaLocalizacaoGestor.getLongitude());

          gestorRepository.save(g);

          log.info(
                  "A localização do Gestor de ID " + record.key() + " foi atualizada!"
          );

        }, () -> log.error(
                "O Gestor de ID " + record.key() + " não existe!"
                )
        );
    }
}
