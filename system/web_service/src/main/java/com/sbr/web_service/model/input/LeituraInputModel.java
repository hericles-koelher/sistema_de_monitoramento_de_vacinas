package com.sbr.web_service.model.input;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeituraInputModel {
    private double temperatura;
    private double coordenadaX;
    private double coordenadaY;
    private Long idCamara;
}
