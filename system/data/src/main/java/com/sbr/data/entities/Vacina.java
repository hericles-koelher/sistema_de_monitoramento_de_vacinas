package com.sbr.data.entities;

import lombok.*;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Vacina {
    @Id
    @GeneratedValue(generator = "vacina_sequence")
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private Duration tolerancia;

    @Column(nullable = false)
    private Double temperaturaMinima;

    @Column(nullable = false)
    private Double temperaturaMaxima;

    public Vacina(@NonNull String nome, @NonNull Duration tolerancia,
                  double temperaturaMinima, double temperaturaMaxima
    ) {
        this.nome = nome;
        this.tolerancia = tolerancia;
        this.temperaturaMinima = temperaturaMinima;
        this.temperaturaMaxima = temperaturaMaxima;
    }
}
