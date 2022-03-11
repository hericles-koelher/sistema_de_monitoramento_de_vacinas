package com.admin_notificacoes.admin_notificacoes;

import com.admin_notificacoes.admin_notificacoes.services.DistanceCalculator;
import com.admin_notificacoes.admin_notificacoes.services.MessageSender;
import com.sbr.data.entities.Camara;
import com.sbr.data.entities.Gestor;
import com.sbr.data.repositories.CamaraRepository;
import com.sbr.kafka_topic_data.entities.Notificacao;
import com.sbr.kafka_topic_data.utils.KafkaEntityDeserializer;
import com.sbr.kafka_topic_data.utils.KafkaEntitySerializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
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
    private final MessageSender messageSender;
    private final DistanceCalculator distanceCalculator;

    private static final Serde<Long> DEFAULT_KEY_SERDE = Serdes.Long();

    private static final Serde<Notificacao> NOTIFICACAO_VALUE_SERDE = Serdes.serdeFrom(
            new KafkaEntitySerializer<>(),
            new KafkaEntityDeserializer<>()
    );

    @Autowired
    void processor(StreamsBuilder streamsBuilder){
        streamsBuilder
                .stream(
                        notificacoesTopicName,
                        Consumed.with(DEFAULT_KEY_SERDE, NOTIFICACAO_VALUE_SERDE)
                )
                .foreach((idCamara, notificacao) -> camaraRepository
                        .findByIdWithGestoresLoaded(idCamara).ifPresent(
                                camara -> {
                                    switch (notificacao.getTipoNotificacao()){
                                        case Atencao:
                                            handleNotificacaoAtencao(notificacao, camara);
                                            break;
                                        case Alerta:
                                            handleNotificacaoAlerta(notificacao, camara);
                                            break;
                                        case Descarte:
                                            handleNotificacaoDescarte(notificacao, camara);
                                            break;
                                    }
                                })
                );
    }

    private void handleNotificacaoAtencao(Notificacao notificacao, Camara camara){
        log.info("Notificação de baixa prioridade detectada. Enviando email...");

        var message = "Atenção gestor, a câmara " + camara.getId()
                + " está apresentando uma temperatura de " + notificacao.getTemperatura()
                + "ºC. Favor comparecer nas seguintes coordenadas:\n\tLatitude = "
                + notificacao.getLatitude()
                + "\n\tLongitude = " + notificacao.getLongitude();

        try {
            messageSender.sendMessage(message, new ArrayList<>(camara.getGestores()));

            log.info("Um e-mail de atenção foi enviado a todos os gestores.");
        } catch (IOException e) {
            log.error("Encontramos um problema ao enviar as mensagens...");
        }
    }

    private void handleNotificacaoAlerta(Notificacao notificacao, Camara camara){
        log.info("Notificação de alta prioridade detectada. Enviando email...");

        String message;

        var nearestGestor = getNearestGestor(
                camara.getGestores(),
                notificacao.getLatitude(),
                notificacao.getLongitude()
        );

        message = "Atenção gestor, a câmara " + camara.getId()
                + " está apresentando uma temperatura de "
                + notificacao.getTemperatura()
                + "ºC, que é uma temperatura fora dos limites aceitaveis."
                + " Favor comparecer nas seguintes coordenadas:\n\tLatitude = "
                + notificacao.getLatitude()
                + "\n\tLongitude = " + notificacao.getLongitude();

        try {
            // O gestor nunca será null, mas o lint do intellij estava reclamando,
            // então resolvi com isso XD.
            assert nearestGestor != null;

            messageSender.sendMessage(message, List.of(nearestGestor));

            log.info("Um e-mail de alerta foi enviado ao gestor " + nearestGestor.getNome() + ".");
        } catch (IOException e) {
            log.error("Encontramos um problema ao enviar as mensagens...");
        }
    }

    private void handleNotificacaoDescarte(Notificacao notificacao, Camara camara){
        log.info("Notificação de alta prioridade detectada. Enviando email...");

        String message;

        message = "Atenção gestor, favor descartar o conteudo da câmara "
                + camara.getId() + ". Para resolver está situação, favor comparecer nas "
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
