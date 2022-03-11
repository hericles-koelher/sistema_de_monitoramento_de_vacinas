package com.sbr.data.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Gestor {
    @Id
    @GeneratedValue(generator = "gestor_sequence")
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    private Double latitude;

    private Double longitude;

    @ManyToMany(mappedBy = "gestores")
    private Set<Camara> camaras;

    public Gestor(@NonNull String nome, @NonNull String email) {
        this.nome = nome;
        this.email = email;
    }
}