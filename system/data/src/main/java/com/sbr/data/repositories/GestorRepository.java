package com.sbr.data.repositories;

import com.sbr.data.entities.Gestor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GestorRepository extends JpaRepository<Gestor, Long> {
    Optional<Gestor> findByEmail(String email);

    @Query("SELECT g FROM Gestor g JOIN FETCH g.camaras WHERE g.id = :id")
    Optional<Gestor> findByIdWithCamarasLoaded(@Param("id")Long id);
}