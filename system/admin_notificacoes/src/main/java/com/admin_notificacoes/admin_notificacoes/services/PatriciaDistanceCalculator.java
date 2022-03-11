package com.admin_notificacoes.admin_notificacoes.services;

import org.springframework.stereotype.Service;

// Dei esse nome pq quem arrumou a função pra
// calcular a distancia foi a professora kkkk
@Service
public class PatriciaDistanceCalculator implements DistanceCalculator{
    // Raio da Terra em metros
    static private final int EARTH_RADIUS = 6371000;

    @Override
    public double calculate(
            double latitude1, double longitude1,
            double latitude2, double longitude2) {

        var phi1 = latitude1 * Math.PI / 180;
        var phi2 = latitude2 * Math.PI / 180;

        var deltaPhi = (phi1 - phi2) * Math.PI / 180;
        var deltaLambda = (longitude1 - longitude2) * Math.PI / 180;

        var a = Math.sin(deltaPhi/2) * Math.sin(deltaPhi/2) +
                Math.cos(phi1) * Math.cos(phi2) *
                        Math.sin(deltaLambda/2) * Math.sin(deltaLambda/2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return EARTH_RADIUS * c;
    }
}
