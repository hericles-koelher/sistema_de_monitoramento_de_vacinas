package com.admin_notificacoes.admin_notificacoes.services;

import com.sbr.data.entities.Gestor;

import java.io.IOException;
import java.util.List;

public interface MessageSender {
    void sendMessage(String message, List<Gestor> receiverList) throws IOException;
}
