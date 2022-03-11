package com.sbr.data.entities;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Leitura {
    @Id
    @GeneratedValue(generator = "leitura_sequence")
    private Long id;

    @Column(nullable = false, updatable = false)
    private LocalDateTime data;

    @Column(nullable = false, updatable = false)
    private double temperatura;

    @Column(nullable = false, updatable = false)
    private double latitude;

    @Column(nullable = false, updatable = false)
    private double longitude;

    @ManyToOne
    @JoinColumn(name = "camara_id", nullable = false)
    private Camara camara;

    public Leitura(@NonNull LocalDateTime data, double temperatura,
                   double latitude, double longitude,
                   @NonNull Camara camara
    ) {
        this.data = data;
        this.temperatura = temperatura;
        this.latitude = latitude;
        this.longitude = longitude;
        this.camara = camara;
    }
}