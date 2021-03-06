package com.sbr.admin_camaras;

import com.sbr.data.entities.Leitura;
import com.sbr.data.repositories.CamaraRepository;
import com.sbr.data.repositories.LeituraRepository;
import com.sbr.data.repositories.LoteRepository;
import com.sbr.kafka_topic_data.entities.LeituraCamara;
import com.sbr.kafka_topic_data.entities.Notificacao;
import com.sbr.kafka_topic_data.enums.TipoNotificacao;
import com.sbr.kafka_topic_data.utils.KafkaEntityDeserializer;
import com.sbr.kafka_topic_data.utils.KafkaEntitySerializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class LeiturasStreamProcessor {
    @Value("${leiturasCamaras.topic}")
    private String leiturasCamarasTopicName;

    @Value("${notificacoes.topic}")
    private String notificacoesTopicName;

    private final CamaraRepository camaraRepository;
    private final LeituraRepository leituraRepository;
    private final LoteRepository loteRepository;

    private static final Serde<Long> DEFAULT_KEY_SERDE = Serdes.Long();
    
    private static final Serde<Notificacao> NOTIFICACAO_VALUE_SERDE = Serdes.serdeFrom(
            new KafkaEntitySerializer<>(),
            new KafkaEntityDeserializer<>()
    );

    private static final Serde<LeituraCamara> LEITURA_VALUE_SERDE = Serdes.serdeFrom(
            new KafkaEntitySerializer<>(),
            new KafkaEntityDeserializer<>()
    );

    @Autowired
    void process(StreamsBuilder streamsBuilder) {
        KStream<Long, LeituraCamara> streamLeiturasCamaras = streamsBuilder
                .stream(
                        leiturasCamarasTopicName,
                        Consumed.with(DEFAULT_KEY_SERDE, LEITURA_VALUE_SERDE)
                );

        streamLeiturasCamaras.filter(
                // Filtrando leituras que possuem uma c??mara valida como alvo...
                (idCamara, leituraCamara) -> camaraRepository.existsById(idCamara)
        ).peek(
                // Salvando no BD cada leitura e atualizando dados da camara.
                        (idCamara, leituraCamara) ->{
                            log.info("Atualizando informa????es do BD...");
                            updateDBInfo(idCamara, leituraCamara);
                        }
        ).filter(
                // Somente as leituras de camaras que possuem um lote que
                // n??o esteja estragado continuar??o na stream...
                (idCamara, leituraCamara) -> camaraRepository
                        .findById(idCamara).filter(
                                camara -> camara.getLote() != null && !camara.getLote().isEstragado()
                        ).isPresent()
        ).split()
                // Criando uma ramifica????o para leituras que est??o proximas do limite...
                .branch(
                        // Condi????o para que uma leitura caia nessa ramifica????o.
                        (idCamara, leituraCamara) -> camaraRepository
                                .findById(idCamara).filter(
                                        camara -> LeituraUtils.isNearLimit(
                                                camara.getTemperaturaAtual(),
                                                camara.getLote().getVacina()
                                        )
                        ).isPresent(),
                        // Consumo da ramifica????o...
                        Branched.withConsumer(
                                // Ser??o enviadas mensagens ao topico de notifica????es de baixa prioridade.
                                streamLeiturasProximasDosLimites -> streamLeiturasProximasDosLimites
                                        // Somente as camaras sem leituras proximas do limite
                                        // recentemente passar??o por esse filtro.
                                        .filter(
                                                (idCamara, leituraCamara) -> camaraRepository
                                                        .findById(idCamara).filter(
                                                                camara -> !camara.getLote().isEmObservacao()
                                                        ).isPresent()
                                        )
                                        .peek(
                                                (idCamara, leituraCamara) -> camaraRepository
                                                        .findById(idCamara).ifPresent(
                                                                camara -> {
                                                                    log.info(
                                                                            String.format("Atualizando status da c??mara %d para observa????o...", camara.getId())
                                                                    );
                                                                    camara.getLote().setEmObservacao(true);
                                                                }
                                                        )
                                        )
                                        .map(
                                                (idCamara, leituraCamara) -> KeyValue.pair(
                                                        idCamara,
                                                        new Notificacao(
                                                                TipoNotificacao.Atencao,
                                                                leituraCamara.getTemperatura(),
                                                                leituraCamara.getLatitude(),
                                                                leituraCamara.getLongitude()
                                                        )
                                                )
                                        ).to(
                                                notificacoesTopicName,
                                                Produced.with(DEFAULT_KEY_SERDE, NOTIFICACAO_VALUE_SERDE)
                                        )
                        )
                )
                // Criando uma ramifica????o para leituras que est??o fora dos limites...
                .branch(
                        // Condi????o para que uma leitura caia nessa ramifica????o.
                        (idCamara, leituraCamara) -> camaraRepository
                                .findById(idCamara).filter(
                                        camara -> LeituraUtils.isOffLimits(
                                                camara.getTemperaturaAtual(),
                                                camara.getLote().getVacina()
                                        )
                        ).isPresent(),
                        // Consumo da ramifica????o...
                        Branched.withConsumer(
                                streamLeiturasForaDosLimites -> streamLeiturasForaDosLimites
                                        .flatMapValues(
                                                this::mapLeituraIntoNotificacaoAP
                                        ).to(
                                                notificacoesTopicName,
                                                Produced.with(DEFAULT_KEY_SERDE, NOTIFICACAO_VALUE_SERDE)
                                        )
                        )
                )
                // Ramifica????o de leituras comuns...
                .defaultBranch(
                        Branched.withConsumer(
                                // Para cada leitura vai resetar o status de problematico
                                // do lote.
                                streamLeiturasNormais -> streamLeiturasNormais.foreach(
                                        this::handleLeiturasNormais
                                )
                        )
                );
    }

    void updateDBInfo(Long idCamara, LeituraCamara leitura){
        camaraRepository.findById(idCamara)
               .ifPresent(camara -> {
                   camara.setTemperaturaAtual(leitura.getTemperatura());

                   camaraRepository.save(camara);

                   leituraRepository.save(
                           new Leitura(
                                   LocalDateTime.now(),
                                   leitura.getTemperatura(),
                                   leitura.getLatitude(),
                                   leitura.getLongitude(),
                                   camara
                           )
                   );
               });
    }

    private List<Notificacao> mapLeituraIntoNotificacaoAP(Long idCamara, LeituraCamara leitura){
        log.info("Mapeando leitura para notifica????o de alta prioridade...");

        Optional<List<Notificacao>> optionalList = camaraRepository
                .findById(idCamara)
                .map(camara -> {
                    var loteCamara = camara.getLote();
                    var vacinaCamara = loteCamara.getVacina();

                    var registroProblematico = loteCamara.getPrimeiroRegistroProblematico();

                    // Este lote j?? possui um registro de problema recente...
                    if(registroProblematico != null){
                        Duration tempoForaDosLimites = Duration.between(
                                registroProblematico,
                                LocalDateTime.now()
                        );

                        // Se o tempo fora das temperaturas aceitaveis for maior que a tolerancia,
                        // ent??o crie uma notifica????o do tipo descarte.
                        // Caso o tempo fora das temperaturas aceitaveis seja menor que a tolerancia,
                        // n??o ?? necess??rio tomar nenhuma nova a????o.
                        if(tempoForaDosLimites.compareTo(vacinaCamara.getTolerancia()) > 0){
                            log.warn(String.format("O lote %d deve ser descartado!", loteCamara.getId()));

                            loteCamara.setEstragado(true);

                            loteRepository.save(loteCamara);

                            return List.of(
                                    new Notificacao(
                                            TipoNotificacao.Descarte,
                                            leitura.getTemperatura(),
                                            leitura.getLatitude(),
                                            leitura.getLongitude()
                                    )
                            );
                        }else{
                            log.info("Uma notifica????o de alerta j?? foi disparada anteriormente...");

                            return List.of();
                        }

                    }else{
                        // Este lote n??o possui nenhum registro de problemas recentes,
                        // ent??o salve o primeiro registro problematico recente e
                        // envie uma notifica????o de Alerta.
                        loteCamara.setPrimeiroRegistroProblematico(
                                LocalTime.now()
                        );

                        loteRepository.save(loteCamara);

                        log.info("Notifica????o de alerta ser?? disparada...");

                        return List.of(
                                new Notificacao(
                                        TipoNotificacao.Alerta,
                                        leitura.getTemperatura(),
                                        leitura.getLatitude(),
                                        leitura.getLongitude()
                                )
                        );
                    }
                });

        return optionalList.orElseGet(List::of);
    }

    private void handleLeiturasNormais(Long idCamara, LeituraCamara leitura){
        camaraRepository.findById(idCamara).ifPresent(
                camara -> {
                    // Pode ser null...
                    var lote = camara.getLote();

                    if(lote != null){
                        boolean flag = false;

                        if(lote.isEmObservacao()){
                            lote.setEmObservacao(false);
                            flag = true;
                        }

                        if(lote.getPrimeiroRegistroProblematico() != null){
                            lote.setPrimeiroRegistroProblematico(null);
                            flag = true;
                        }

                        if(flag){
                            loteRepository.save(lote);
                        }
                    }
                }
        );
    }
}
