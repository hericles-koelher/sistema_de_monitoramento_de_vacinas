package com.sbr.data.repositories;

import com.sbr.data.entities.Lote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoteRepository extends JpaRepository<Lote, Long> {
}