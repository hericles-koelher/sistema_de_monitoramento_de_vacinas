package com.sbr.web_service.model.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CamaraOutputModel {
    private Long id;
    private Double temperaturaAtual;
    private Long idLote;
}
