package com.sbr.web_service.model.output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NotificacaoOutputModel {
    private Long id;
    private Long idCamara;
    private boolean atendida;
}
