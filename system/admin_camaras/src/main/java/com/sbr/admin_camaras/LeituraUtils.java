package com.sbr.admin_camaras;

import com.sbr.data.entities.Vacina;

public class LeituraUtils {
    public static boolean isOffLimits(Double temperatura, Vacina vacina){
        var tempMin = vacina.getTemperaturaMinima();
        var tempMax = vacina.getTemperaturaMaxima();

        return temperatura <= tempMin || temperatura >= tempMax;
    }

    public static boolean isNearLimit(Double temperatura, Vacina vacina){
        var tempMin = vacina.getTemperaturaMinima();
        var tempMax = vacina.getTemperaturaMaxima();

        // Temperatura proxima do limite minimo
        if(temperatura > tempMin && temperatura <= tempMin + 1.0){
            return true;
        }

        // Temperatura proxima do limite maximo
        if(temperatura < tempMax && temperatura >= tempMax + 1.0){
            return true;
        }

        return false;
    }
}
