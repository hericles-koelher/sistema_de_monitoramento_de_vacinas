package com.sbr.data.repositories;

import com.sbr.data.entities.Camara;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CamaraRepository extends JpaRepository<Camara, Long> {
    @Query("SELECT c FROM Camara c JOIN FETCH c.gestores WHERE c.id = :id")
    Optional<Camara> findByIdWithGestoresLoaded(@Param("id")Long id);
}