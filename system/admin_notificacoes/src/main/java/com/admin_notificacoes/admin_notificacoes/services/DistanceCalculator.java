package com.admin_notificacoes.admin_notificacoes.services;

public interface DistanceCalculator {
    double calculate(double latitude1, double longitude1,
                     double latitude2, double longitude2);
}
