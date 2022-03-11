package com.sbr.web_service.model.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VacinaOutputModel {
    private Long id;
    private String nome;
    private int toleranciaEmMinutos;
    private Double temperaturaMinima;
    private Double temperaturaMaxima;
}
