package com.sbr.data.entities;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Lote {
    @Id
    @GeneratedValue(generator = "lote_sequence")
    private Long id;

    @Column(nullable = false, updatable = false)
    private LocalDate validade;

    private LocalTime primeiroRegistroProblematico;

    @Column(nullable = false)
    private boolean estragado;

    @Column(nullable = false)
    private boolean emObservacao;

    @ManyToOne
    @JoinColumn(name = "vacina_id", nullable = false, updatable = false)
    private Vacina vacina;

    public Lote(@NonNull LocalDate validade, @NonNull Vacina vacina) {
        this.validade = validade;
        this.vacina = vacina;
        this.estragado = false;
        this.emObservacao = false;
    }
}