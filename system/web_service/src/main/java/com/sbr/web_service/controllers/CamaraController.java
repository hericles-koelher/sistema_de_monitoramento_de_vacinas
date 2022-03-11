package com.sbr.web_service.controllers;

import com.sbr.data.entities.Camara;
import com.sbr.data.repositories.CamaraRepository;
import com.sbr.data.repositories.GestorRepository;
import com.sbr.data.repositories.LoteRepository;
import com.sbr.web_service.model.input.CamaraInputModel;
import com.sbr.web_service.model.output.CamaraOutputModel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/camaras")
@Slf4j
@AllArgsConstructor
public class CamaraController {
    private final CamaraRepository camaraRepository;
    private final LoteRepository loteRepository;
    private final GestorRepository gestorRepository;

    static Double TEMPERATURA_INICIAL = 18.0;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public @ResponseBody CamaraOutputModel createCamara(){
        log.info("Cadastrando uma nova câmara.");
        var novaCamara = new Camara();

        novaCamara.setTemperaturaAtual(TEMPERATURA_INICIAL);

        var newCamara = camaraRepository.saveAndFlush(
                novaCamara
        );

        log.info("Câmara cadastrada com sucesso.");

        return camaraEntityToOutputModel(newCamara);
    }

    @GetMapping
    public @ResponseBody List<CamaraOutputModel> getCamaras(){
        log.info("Buscando câmaras.");

        return camaraOutputModels(
                camaraRepository.findAll()
        );
    }

    @GetMapping("/{id}")
    public @ResponseBody CamaraOutputModel getCamaraById(
            @PathVariable(value = "id") Long id) {
        log.info("Buscando câmara " + id + ".");

        var camara = camaraRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        log.info("Câmara encontrada com sucesso.");

        return camaraEntityToOutputModel(camara);
    }

    @PutMapping("/{id}/atualizarLote")
    public @ResponseBody CamaraOutputModel updateLoteFromCamara(
            @PathVariable("id") Long camaraId,
            @RequestBody(required = false)CamaraInputModel camaraInputModel){
        log.info("Atualizando lote da câmara " + camaraId +  ".");

        var camara = camaraRepository.findById(camaraId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        if(camaraInputModel == null){
            camara.setLote(null);
            log.info("Lote da câmara redefinida.");
        }else{
            camara.setLote(
                    loteRepository.findById(camaraInputModel.getIdLote()).orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST)
                    )
            );

            log.info("Lote da câmara atualizada.");
        }

        camara = camaraRepository.save(camara);

        return camaraEntityToOutputModel(camara);
    }

    @PostMapping("/{idCamara}/gestor/{idGestor}")
    public @ResponseBody void adicionarGestorResponsavel(
            @PathVariable("idCamara") Long idCamara,
            @PathVariable("idGestor") Long idGestor
    ){
        log.info("Cadastrando gestor " + idGestor + " como responsavel pela câmara " + idCamara);

        // Poderia melhorar os retornos abaixo, de maneira que ficasse mais claro
        // o que exatamente ocasionou o problema, mas tenho outras prioridades agora...

        var camara = camaraRepository.findById(idCamara).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        var gestor = gestorRepository.findById(idGestor).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        camara.getGestores().add(gestor);
        camaraRepository.save(camara);
    }

    @DeleteMapping("/{idCamara}/gestor/{idGestor}")
    public @ResponseBody void removerGestorResponsavel(
            @PathVariable("idCamara") Long idCamara,
            @PathVariable("idGestor") Long idGestor
    ){
        log.info("Removendo gestor " + idGestor + " da lista de responsaveis pela câmara " + idCamara);

        // Poderia melhorar os retornos abaixo, de maneira que ficasse mais claro
        // o que exatamente ocasionou o problema, mas tenho outras prioridades agora...

        var camara = camaraRepository.findById(idCamara).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        var gestor = gestorRepository.findById(idGestor).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        camara.getGestores().remove(gestor);
        camaraRepository.save(camara);
    }

    private List<CamaraOutputModel> camaraOutputModels(List<Camara> camaras){
        return camaras
                .stream()
                .map(this::camaraEntityToOutputModel)
                .collect(Collectors.toList());
    }

    private CamaraOutputModel camaraEntityToOutputModel(Camara camara){
        Long loteId = null;

        if(camara.getLote() != null){
            loteId = camara.getLote().getId();
        }

        return new CamaraOutputModel(
                camara.getId(),
                camara.getTemperaturaAtual(),
                loteId
        );
    }
}
