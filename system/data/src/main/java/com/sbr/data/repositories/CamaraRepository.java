package com.sbr.data.repositories;

import com.sbr.data.entities.Camara;
import com.sbr.data.entities.Gestor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CamaraRepository extends JpaRepository<Camara, Long> {
    @Query("SELECT c FROM Camara c JOIN FETCH c.gestores WHERE c.id = :id")
    Optional<Camara> findByIdWithGestoresLoaded(@Param("id")Long id);

    @Query("SELECT c FROM Camara c JOIN FETCH c.gestores WHERE :gestor MEMBER OF c.gestores")
    List<Camara> findAllCamarasWithGestor(@Param("gestor") Gestor gestor);
}