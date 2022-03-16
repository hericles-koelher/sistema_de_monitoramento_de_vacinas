package com.sbr.kafka_topic_data.entities;

import com.sbr.kafka_topic_data.enums.TipoNotificacao;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notificacao {
    private TipoNotificacao tipoNotificacao;
    private double temperatura;
    private double latitude;
    private double longitude;

    public Notificacao(TipoNotificacao tipoNotificacao) {
        this.tipoNotificacao = tipoNotificacao;
    }
}
