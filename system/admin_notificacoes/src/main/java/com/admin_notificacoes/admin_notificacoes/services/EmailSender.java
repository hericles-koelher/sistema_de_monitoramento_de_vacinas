package com.admin_notificacoes.admin_notificacoes.services;

import com.sbr.data.entities.Gestor;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class EmailSender implements MessageSender{
    private final SendGrid sendGrid;
    private final Email sender;

    public EmailSender() {
        // obtem uma instancia de sendgrid recuperando a chave da api
        // registrada nas variaveis de ambiente
        sendGrid = new SendGrid(
                System.getenv("SENDGRID_API_KEY")
        );

        // isso aqui dá pra mudar dps, só coloquei pra testar rapido msm
        sender = new Email("brunokoelher@hotmail.com");
    }

    @Override
    public void sendMessage(String message, List<Gestor> receiverList) throws IOException {
        var content = new Content("text/plain", message);

        var personalization = new Personalization();
        for (var receiver: receiverList) {
            personalization.addTo(
                    new Email(receiver.getEmail())
            );
        }

        var mail = new Mail();
        mail.setFrom(sender);
        mail.setSubject("Notificação das Câmaras");
        mail.addPersonalization(personalization);
        mail.addContent(content);

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sendGrid.api(request);

        // Só pra testes...
        System.out.println(response.getStatusCode());
        System.out.println(response.getBody());
        System.out.println(response.getHeaders());
    }
}
