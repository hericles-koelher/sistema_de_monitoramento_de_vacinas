package com.sbr.web_service.model.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GestorOutputModel {
    private Long id;
    private String nome;
    private String email;
    private Double coordenadaX;
    private Double coordenadaY;
}
