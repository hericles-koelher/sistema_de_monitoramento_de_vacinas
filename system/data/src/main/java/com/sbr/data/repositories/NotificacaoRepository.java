package com.sbr.data.repositories;

import com.sbr.data.entities.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    List<Notificacao> findAllByCamaraId(Long camara_id);
}