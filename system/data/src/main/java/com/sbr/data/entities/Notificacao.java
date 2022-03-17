package com.sbr.data.entities;

import com.sbr.data.enums.TipoNotificacao;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Notificacao {
    @Id
    @GeneratedValue(generator = "notificacao_sequence")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "camara_id", nullable = false, updatable = false)
    private Camara camara;

    private TipoNotificacao tipoNotificacao;

    private boolean atendida;

    public Notificacao(@NonNull Camara camara, TipoNotificacao tipoNotificacao) {
        this.camara = camara;
        this.tipoNotificacao = tipoNotificacao;
        this.atendida = false;
    }
}
