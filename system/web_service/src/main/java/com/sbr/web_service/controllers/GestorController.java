package com.sbr.web_service.controllers;

import com.sbr.data.entities.Gestor;
import com.sbr.data.repositories.GestorRepository;
import com.sbr.kafka_topic_data.entities.LocalizacaoGestor;
import com.sbr.web_service.model.input.GestorInputModel;
import com.sbr.web_service.model.output.GestorOutputModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("api/v1/gestores")
@Slf4j
@RequiredArgsConstructor
public class GestorController {
    @Value("${localizacaoGestores.topic}")
    private String localizacaoGestoresTopicName;

    private final KafkaTemplate<Long, LocalizacaoGestor> kafkaTemplate;
    private final GestorRepository gestorRepository;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public @ResponseBody GestorOutputModel create(
            @RequestBody GestorInputModel gestorInputModel){
        if(gestorRepository
                .findByEmail(gestorInputModel.getEmail())
                .isPresent()){
            log.error("Tentativa de criar gestor com e-mail já cadastrado.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        var gestor = gestorRepository.save(
                new Gestor(
                        gestorInputModel.getNome(),
                        gestorInputModel.getEmail()
                )
        );

        log.info("Gestor " + gestor.getNome() + " criado com sucesso.");

        return gestorEntityToOutputModel(gestor);
    }

    @PutMapping("/atualizarLocalizacao/{id}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public String atualizarLocalizacao(
            @RequestBody LocalizacaoGestor localizacaoGestor,
            @PathVariable("id") Long idGestor
    ){
       try{
           log.info("Tentando atualizar localização do gestor.");

           kafkaTemplate.send(
                   localizacaoGestoresTopicName,
                   idGestor,
                   localizacaoGestor
           );

           return "A localização do gestor será atualizada!";
       } catch (KafkaException e){
           throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

    @GetMapping("/identificarPorEmail")
    public GestorOutputModel identificarPorTelefone(
            @RequestParam(name = "email") String email){
        log.info("Buscando gestor por e-mail.");

        var gestor = gestorRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> {
                            log.error("Tentativa de encontrar gestor com e-mail não cadastrado.");
                            return new ResponseStatusException(HttpStatus.NOT_FOUND);
                        }
                );

        log.info("Gestor com email " + email + " encontrado.");

        return gestorEntityToOutputModel(gestor);
    }

    private GestorOutputModel gestorEntityToOutputModel(Gestor gestor){
        return new GestorOutputModel(
                gestor.getId(),
                gestor.getNome(),
                gestor.getEmail(),
                gestor.getLatitude(),
                gestor.getLongitude()
        );
    }
}
