package com.sbr.web_service.model.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LeituraOutputModel {
    private Long id;
    private LocalDateTime data;
    private Double temperatura;
    private Double coordenadaX;
    private Double coordenadaY;
    private Long idCamara;
}
