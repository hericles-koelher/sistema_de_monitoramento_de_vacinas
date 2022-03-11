package com.sbr.data.entities;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
public class Camara {
    @Id
    @GeneratedValue(generator = "camara_sequence")
    private Long id;

    private Double temperaturaAtual;

    @ManyToOne
    @JoinColumn(name = "lote_id")
    private Lote lote;

    @ManyToMany
    @JoinTable(
            name = "camara_gestor",
            joinColumns = @JoinColumn(name = "camara_id"),
            inverseJoinColumns = @JoinColumn(name = "gestor_id")
    )
    private Set<Gestor> gestores;
}