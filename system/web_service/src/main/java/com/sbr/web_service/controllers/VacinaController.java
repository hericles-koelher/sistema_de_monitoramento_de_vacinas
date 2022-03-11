package com.sbr.web_service.controllers;

import com.sbr.data.entities.Vacina;
import com.sbr.data.repositories.VacinaRepository;
import com.sbr.web_service.model.input.VacinaInputModel;
import com.sbr.web_service.model.output.VacinaOutputModel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/vacinas")
@AllArgsConstructor
@Slf4j
public class VacinaController {
    private final VacinaRepository vacinaRepository;

    @PostMapping
    public @ResponseBody VacinaOutputModel createVacina(
            @RequestBody VacinaInputModel vacinaInputModel){
        log.info("Criando vacina.");

        try{
            var vacina = vacinaRepository.save(
                    new Vacina(
                            vacinaInputModel.getNome(),
                            Duration.ofMinutes(Long.parseLong(vacinaInputModel.getToleranciaEmMinutos())),
                            vacinaInputModel.getTemperaturaMinima(),
                            vacinaInputModel.getTemperaturaMaxima()
                    )
            );

            log.info("Vacina criada com sucesso.");

            return vacinaEntityToOutputModel(vacina);
        } catch (NumberFormatException e){
            log.error("Erro ocasionado por problemas ao fazer o parse da tolerancia.");

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public @ResponseBody List<VacinaOutputModel> getVacinas(){
        log.info("Buscando todas as vacinas.");

        return vacinaOutputModels(
                vacinaRepository.findAll()
        );
    }

    @GetMapping("/{id}")
    public @ResponseBody
    VacinaOutputModel getVacinaById(@PathVariable(value = "id") Long id) {
        log.info("Buscando vacina de ID = " + id + ".");

        Vacina vacina = vacinaRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Vacina de ID = " + id + " não encontrada.");
                    return new ResponseStatusException(HttpStatus.NOT_FOUND);
                }
        );

        log.info("Vacina encontrada.");

        return vacinaEntityToOutputModel(vacina);
    }

    private List<VacinaOutputModel> vacinaOutputModels(List<Vacina> vacinas){
        return vacinas
                .stream()
                .map(this::vacinaEntityToOutputModel)
                .collect(Collectors.toList());
    }

    private VacinaOutputModel vacinaEntityToOutputModel(Vacina vacina){
        return new VacinaOutputModel(
                vacina.getId(),
                vacina.getNome(),
                (int) vacina.getTolerancia().toMinutes(),
                vacina.getTemperaturaMinima(),
                vacina.getTemperaturaMaxima()
        );
    }
}
