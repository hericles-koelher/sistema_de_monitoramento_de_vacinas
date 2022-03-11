package com.sbr.simulador.entities;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeituraCamara {
    private double temperatura;
    private double latitude;
    private double longitude;
}
