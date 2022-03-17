package com.sbr.web_service.controllers;

import com.sbr.data.entities.Notificacao;
import com.sbr.data.repositories.NotificacaoRepository;
import com.sbr.web_service.model.output.NotificacaoOutputModel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/notificacoes")
@Slf4j
@AllArgsConstructor
public class NotificacaoController {
    private NotificacaoRepository notificacaoRepository;

    @GetMapping
    public @ResponseBody
    List<NotificacaoOutputModel> getNotificacoes(){
        log.info("Buscando notificações.");

        return notificacaoOutputModels(
                notificacaoRepository.findAll()
        );
    }

    @GetMapping("camara/{id}")
    public @ResponseBody List<NotificacaoOutputModel> getNotificacoesFromCamara(
            @PathVariable(value = "id") Long idCamara
    ){
        log.info("Buscando notificações da câmara que possui ID: " + idCamara);

        return notificacaoOutputModels(
                notificacaoRepository.findAllByCamaraId(idCamara)
        );
    }

    @GetMapping("/{id}")
    public @ResponseBody NotificacaoOutputModel getNotificacao(
            @PathVariable(value = "id") Long idNotificacao
    ){
        log.info("Buscando notificação que possui ID: " + idNotificacao);

        var notificacao = notificacaoRepository.findById(idNotificacao)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
                );

        return notificacaoEntityToOutputModel(notificacao);
    }

    @PutMapping("atender/{idNotificacao}")
    public @ResponseBody NotificacaoOutputModel updateNotificacaoStatus(
            @PathVariable(value = "idNotificacao") Long idNotificacao
    ){
        log.info("Buscando notificação que possui ID: " + idNotificacao);

        var notificacao = notificacaoRepository.findById(idNotificacao)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
                );

        log.info("Atualizando notificação que possui ID: " + idNotificacao);

        notificacao.setAtendida(true);
        notificacaoRepository.save(notificacao);

        return notificacaoEntityToOutputModel(notificacao);
    }

    private List<NotificacaoOutputModel> notificacaoOutputModels(List<Notificacao> notificacoes){
        return notificacoes
                .stream()
                .map(this::notificacaoEntityToOutputModel)
                .collect(Collectors.toList());
    }

    private NotificacaoOutputModel notificacaoEntityToOutputModel(Notificacao notificacao){
        return new NotificacaoOutputModel(
                notificacao.getId(),
                notificacao.getCamara().getId(),
                notificacao.isAtendida()
        );
    }
}
