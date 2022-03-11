package com.sbr.web_service.controllers;

import com.sbr.data.entities.Leitura;
import com.sbr.data.repositories.LeituraRepository;
import com.sbr.kafka_topic_data.entities.LeituraCamara;
import com.sbr.web_service.model.output.LeituraOutputModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/leituras")
@Slf4j
@RequiredArgsConstructor
public class LeituraController {
    @Value("${leiturasCamaras.topic}")
    private String leituraCamarasTopicName;

    private final KafkaTemplate<Long, LeituraCamara> kafkaTemplate;
    private final LeituraRepository leituraRepository;

    @GetMapping
    public @ResponseBody List<LeituraOutputModel> getLeituras(){
        log.info("Buscando todas as leituras.");

        return leituraRepository
                .findAll()
                .stream()
                .map(this::leituraEntityToModel)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public @ResponseBody
    LeituraOutputModel getLeituraById(
            @PathVariable(value = "id") Long leituraId){
        log.info("Buscando leitura de ID = " + leituraId + ".");

        var leitura = leituraRepository.findById(leituraId).orElseThrow(
                () -> {
                    log.error("Leitura não encontrada.");
                    return new ResponseStatusException(HttpStatus.NOT_FOUND);
                }
        );

        log.info("Leitura encontrada.");

        return leituraEntityToModel(leitura);
    }

    @GetMapping("/camara/{id}")
    public @ResponseBody List<LeituraOutputModel> getLeiturasFromCamaraById(
            @PathVariable(value = "id") Long camaraId
    ) throws ResponseStatusException {
        log.info("Buscando leituras da câmara de ID = " + camaraId + ".");

        List<Leitura> leituras = leituraRepository.findAllByCamaraId(camaraId);

        return leituraModels(leituras);
    }

    @PostMapping("/camara/{id}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public String saveLeituraCamara(
            @RequestBody LeituraCamara leituraCamara,
            @PathVariable(value = "id") Long idCamara
    ){
        try{
            log.info("Tentando salvar leitura de uma câmara.");

            kafkaTemplate.send(
                    leituraCamarasTopicName,
                    idCamara,
                    leituraCamara
            );

            return "A leitura da câmara será registrada!";
        } catch (KafkaException e){
            log.error("Ocorreu um erro com Kafka.");
            log.error(e.getMessage());
            log.trace(Arrays.toString(e.getStackTrace()));

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private List<LeituraOutputModel> leituraModels(List<Leitura> leituras){
        return leituras
                .stream()
                .map(this::leituraEntityToModel)
                .collect(Collectors.toList());
    }

    private LeituraOutputModel leituraEntityToModel(Leitura leitura){
        return new LeituraOutputModel(
                leitura.getId(),
                leitura.getData(),
                leitura.getTemperatura(),
                leitura.getLatitude(),
                leitura.getLongitude(),
                leitura.getCamara().getId()
        );
    }
}
