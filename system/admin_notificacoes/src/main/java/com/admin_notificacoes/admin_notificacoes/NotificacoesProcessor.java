package com.admin_notificacoes.admin_notificacoes;

import com.admin_notificacoes.admin_notificacoes.services.DistanceCalculator;
import com.admin_notificacoes.admin_notificacoes.services.MessageSender;
import com.sbr.data.entities.Camara;
import com.sbr.data.entities.Gestor;
import com.sbr.data.repositories.CamaraRepository;
import com.sbr.data.repositories.NotificacaoRepository;
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
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificacoesProcessor {

    @Value("${notificacoes.topic}")
    private String notificacoesTopicName;

    private final CamaraRepository camaraRepository;
    private final NotificacaoRepository notificacaoRepository;
    private final MessageSender messageSender;
    private final DistanceCalculator distanceCalculator;

    private static final Serde<Long> DEFAULT_KEY_SERDE = Serdes.Long();

    private static final Serde<Notificacao> NOTIFICACAO_VALUE_SERDE = Serdes.serdeFrom(
            new KafkaEntitySerializer<>(),
            new KafkaEntityDeserializer<>()
    );

    private static final int MINIMUM_NUMBER_OF_INCIDENTS = 7;
    
    private static final int NUMBER_OF_DAYS_TO_ANALYSE = 7;

    @Autowired
    void processor(StreamsBuilder streamsBuilder){
        streamsBuilder
                .stream(
                        notificacoesTopicName,
                        Consumed.with(DEFAULT_KEY_SERDE, NOTIFICACAO_VALUE_SERDE)
                )
                .peek((idCamara, notificacao) -> camaraRepository
                        .findByIdWithGestoresLoaded(idCamara).ifPresent(
                                camara -> {
                                    switch (notificacao.getTipoNotificacao()){
                                        case Atencao:
                                            handleNotificacaoAtencao(notificacao, camara);
                                            break;
                                        case Alerta:
                                            updateDB(com.sbr.data.enums.TipoNotificacao.Alerta, camara);
                                            handleNotificacaoAlerta(notificacao, camara);
                                            break;
                                        case Descarte:
                                            updateDB(com.sbr.data.enums.TipoNotificacao.Descarte, camara);
                                            handleNotificacaoDescarte(notificacao, camara);
                                            break;
                                        case  ProblemasFrequentes:
                                            updateDB(com.sbr.data.enums.TipoNotificacao.ProblemasFrequentes, camara);
                                            handleNotificacaoProblemasFrequentes(camara);
                                    }
                                })
                ).filter(
                        (idCamara, notificacao) ->
                                notificacao.getTipoNotificacao() == TipoNotificacao.Atencao ||
                                        notificacao.getTipoNotificacao() == TipoNotificacao.Descarte
                ).groupByKey().windowedBy(
                        TimeWindows.ofSizeWithNoGrace(
                                Duration.ofDays(NUMBER_OF_DAYS_TO_ANALYSE)
                        )
                ).count().filter(
                        (windowedIdCamara, countResult) -> countResult > MINIMUM_NUMBER_OF_INCIDENTS
                ).toStream()
                .map((windowedKey, countResult) ->
                        KeyValue.pair(
                                windowedKey.key(), 
                                new Notificacao(TipoNotificacao.ProblemasFrequentes)
                        )
                )
                .to(
                        notificacoesTopicName,
                        Produced.with(DEFAULT_KEY_SERDE, NOTIFICACAO_VALUE_SERDE)
                );
    }

    private void updateDB(com.sbr.data.enums.TipoNotificacao tipoNotificacao, Camara camara){
        notificacaoRepository.save(
                new com.sbr.data.entities.Notificacao(
                        camara,
                        tipoNotificacao
                )
        );
    }

    private void handleNotificacaoAtencao(Notificacao notificacao, Camara camara){
        log.info("Notifica????o de baixa prioridade detectada. Enviando email...");

        var message = "Aten????o gestor, a c??mara " + camara.getId()
                + " est?? apresentando uma temperatura de " + notificacao.getTemperatura()
                + "??C. Favor comparecer nas seguintes coordenadas:\n\tLatitude = "
                + notificacao.getLatitude()
                + "\n\tLongitude = " + notificacao.getLongitude();

        try {
            messageSender.sendMessage(message, new ArrayList<>(camara.getGestores()));

            log.info("Um e-mail de aten????o foi enviado a todos os gestores.");
        } catch (IOException e) {
            log.error("Encontramos um problema ao enviar as mensagens...");
        }
    }

    private void handleNotificacaoAlerta(Notificacao notificacao, Camara camara){
        log.info("Notifica????o de alta prioridade detectada. Enviando email...");

        String message;

        var nearestGestor = getNearestGestor(
                camara.getGestores(),
                notificacao.getLatitude(),
                notificacao.getLongitude()
        );

        message = "Aten????o gestor, a c??mara " + camara.getId()
                + " est?? apresentando uma temperatura de "
                + notificacao.getTemperatura()
                + "??C, que ?? uma temperatura fora dos limites aceitaveis."
                + " Favor comparecer nas seguintes coordenadas:\n\tLatitude = "
                + notificacao.getLatitude()
                + "\n\tLongitude = " + notificacao.getLongitude();

        try {
            // O gestor nunca ser?? null, mas o lint do intellij reclamava,
            // ent??o resolvi com isso.
            assert nearestGestor != null;

            messageSender.sendMessage(message, List.of(nearestGestor));

            log.info("Um e-mail de alerta foi enviado ao gestor " + nearestGestor.getNome() + ".");
        } catch (IOException e) {
            log.error("Encontramos um problema ao enviar as mensagens...");
        }
    }

    private void handleNotificacaoDescarte(Notificacao notificacao, Camara camara){
        log.info("Notifica????o de alta prioridade detectada. Enviando email...");

        String message;

        message = "Aten????o gestor, favor descartar o conteudo da c??mara "
                + camara.getId() + ". Para resolver est?? situa????o, favor comparecer nas "
                + "seguintes coordenadas:\n\tLatitude = "
                + notificacao.getLatitude()
                + "\n\tLongitude = " + notificacao.getLongitude();

        try {
            messageSender.sendMessage(message, new ArrayList<>(camara.getGestores()));

            log.info("Um e-mail de descarte foi enviado a todos os gestores.");
        } catch (IOException e) {
            log.error("Encontramos um problema ao enviar as mensagens...");
        }
    }

    private void handleNotificacaoProblemasFrequentes(Camara camara){
        log.info("Notifica????o de alta prioridade detectada. Enviando email...");

        String message;

        message = "Aten????o gestor, a c??mara " + camara.getId()
                + " tem apresentando uma s??rie de "
                + "problemas na ultima semana. Favor solucionar este problema.";

        try {
            messageSender.sendMessage(message, new ArrayList<>(camara.getGestores()));

            log.info("Um e-mail de problemas frequentes foi enviado a todos os gestores.");
        } catch (IOException e) {
            log.error("Encontramos um problema ao enviar as mensagens...");
        }
    }

    private Gestor getNearestGestor(Set<Gestor> gestorList, Double latitude, Double longitude){
        if(gestorList.isEmpty()) return null;

        Gestor nearestGestor = null;
        double nearestGestorDistance = Double.POSITIVE_INFINITY;

        for (var gestor: gestorList) {
            var currentGestorDistance = distanceCalculator.calculate(
                    gestor.getLatitude(), gestor.getLongitude(),
                    latitude, longitude
            );

            if(currentGestorDistance < nearestGestorDistance){
                nearestGestorDistance = currentGestorDistance;
                nearestGestor = gestor;
            }
        }

        return nearestGestor;
    }
}
