package com.sbr.web_service.model.input;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VacinaInputModel {
    private String nome;
    private String toleranciaEmMinutos;
    private double temperaturaMinima;
    private double temperaturaMaxima;
}
