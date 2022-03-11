package com.sbr.data.repositories;

import com.sbr.data.entities.Camara;
import com.sbr.data.entities.Leitura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeituraRepository extends JpaRepository<Leitura, Long> {
    List<Leitura> findAllByCamaraId(Long camara_id);

    Leitura findFirstByCamaraOrderByDataDesc(Camara camara);
}