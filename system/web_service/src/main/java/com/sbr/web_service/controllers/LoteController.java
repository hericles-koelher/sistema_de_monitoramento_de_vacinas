package com.sbr.web_service.controllers;

import com.sbr.data.entities.Lote;
import com.sbr.data.repositories.LoteRepository;
import com.sbr.data.repositories.VacinaRepository;
import com.sbr.web_service.model.input.LoteInputModel;
import com.sbr.web_service.model.output.LoteOutputModel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/lotes")
@AllArgsConstructor
@Slf4j
public class LoteController {
    private final LoteRepository loteRepository;
    private final VacinaRepository vacinaRepository;

    @PostMapping
    public @ResponseBody LoteOutputModel createLote(@RequestBody LoteInputModel loteInputModel){
        log.info("Criando lote.");
        var vacina = vacinaRepository
                .findById(loteInputModel.getVacinaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));

        try{
            var parsedDate = LocalDate.parse(
                    loteInputModel.getValidade(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
            );

            var lote = loteRepository.save(new Lote(parsedDate, vacina));

            log.info("Lote criado com sucesso.");

            return loteEntityToOutputModel(lote);
        } catch (DateTimeParseException e){
            log.error("Erro ocasionado por problemas ao fazer o parse de data.");

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public @ResponseBody List<LoteOutputModel> getLotes(){
        log.info("Buscando todos os lotes.");

        return loteOutputModels(
                loteRepository.findAll()
        );
    }

    @GetMapping("/{id}")
    public @ResponseBody
    LoteOutputModel getLoteById(@PathVariable(value = "id") Long id)
            throws ResponseStatusException {
        log.info("Buscando lote de ID = " + id + ".");

        Lote lote =  loteRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Lote de ID = " + id + " não foi encontrado.");
                    return new ResponseStatusException(HttpStatus.NOT_FOUND);
                }
        );

        log.info("Lote encontrado.");

        return loteEntityToOutputModel(lote);
    }

    private List<LoteOutputModel> loteOutputModels(List<Lote> list){
        return list
                .stream()
                .map(this::loteEntityToOutputModel)
                .collect(Collectors.toList());
    }

    private LoteOutputModel loteEntityToOutputModel(Lote lote){
        return new LoteOutputModel(
                lote.getId(),
                lote.getValidade(),
                lote.getVacina().getId()
        );
    }
}
