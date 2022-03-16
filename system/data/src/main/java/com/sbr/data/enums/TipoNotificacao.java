package com.sbr.data.enums;

public enum TipoNotificacao {
    Alerta,     // Usado quando a temperatura passou dos limites

    Descarte,   // Usado quando um lote de vacina numa
                // determinada câmara deve ser descartado

    ProblemasFrequentes // Usado quando houverem muitos episodios criticos
                        // numa determinada câmara
}
