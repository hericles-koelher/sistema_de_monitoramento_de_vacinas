package com.sbr.simulador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbr.simulador.entities.LeituraCamara;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@RequiredArgsConstructor
public class SimulacaoApplication{    
    public static void main(String[] args) {
        var simulacao = new SimulacaoApplication(
                new ObjectMapper(),
                HttpClient.newHttpClient(),
                new Random()
        );

//        simulacao.simularComportamentoNormal((long) 1);
        simulacao.simularComportamentoForaDoNormal((long) 1);

    }

    static private final double latitudeCT = -20.272808482411644;
    static private final double longitudeCT = -40.30604999408602;
    static private final int sleepTimeMS = 3000;

    // Carinha responsavel por realizar a conversão para JSON
    private final ObjectMapper mapper;

    // Nosso client http
    private final HttpClient client;

    private final Random rng;

    public void simularComportamentoNormal(Long idCamara){
        log.info(
                "Simulando comportamento esperado de uma câmara. A câmara utilizada possui ID " + idCamara
        );

        while (true){
            var leitura = new LeituraCamara(
                    // Vai gerar as temperaturas 11º,12º,13º e 14º
                    11.0 + rng.nextInt(4),
                    latitudeCT,
                    longitudeCT
            );

            log.info(
                    "Enviando leitura com temperatura de " + String.format("%.1f", leitura.getTemperatura()) + "º"
            );

            try {
                sendLeitura(idCamara, leitura);
                // Tirando um cochilo antes da proxima leitura....
                Thread.sleep(sleepTimeMS);
            } catch (IOException | InterruptedException e) {
                log.error(e.getMessage());
                log.trace(Arrays.toString(e.getStackTrace()));
            }
        }
    }

    public void simularComportamentoForaDoNormal(Long idCamara){
        int percentualErro = 90;
        int temperaturaMinima = 10;
        int temperaturaMaxima = 15;
        int diff = temperaturaMaxima - temperaturaMinima;

        ArrayList<Integer> erro = new ArrayList<>();
        for(int i=0 ; i < percentualErro; i++)
            erro.add(rng.nextInt(101));

        while(true){
            int n = rng.nextInt(101);

            double temperatura;

            if(erro.contains(n)){ //temperatura problemática
                if(n < 50){ //temperatura menor que o ideal (o uso do diff como faixa é arbitrário)

                    while(true){
                        temperatura = temperaturaMinima - rng.nextInt(diff+1) - rng.nextFloat();

                        log.info("A temperatura está abaixo do limite mínimo: " + String.format("%.1f", temperatura) + '°');

                        try {
                            sendLeitura(
                                    idCamara,
                                    new LeituraCamara(
                                            temperatura,
                                            latitudeCT,
                                            longitudeCT
                                    )
                            );
                            Thread.sleep(sleepTimeMS);
                        } catch (IOException | InterruptedException e) {
                            log.error(e.getMessage());
                        }
                    }
                }
                else{  //temperatura maior que o ideal
                    while(true){
                        temperatura = temperaturaMaxima + rng.nextInt(diff+1) + rng.nextFloat();

                        log.info("A temperatura está acima do limite máximo: " + String.format("%.1f", temperatura) + '°');

                        try {
                            sendLeitura(
                                    idCamara,
                                    new LeituraCamara(
                                            temperatura,
                                            latitudeCT,
                                            longitudeCT
                                    )
                            );
                            Thread.sleep(sleepTimeMS);
                        } catch (IOException | InterruptedException e) {
                            log.error(e.getMessage());
                        }
                    }
                }
            }
            else{ //temperatura normal
                temperatura = temperaturaMinima + rng.nextInt(diff+1) + rng.nextFloat();

                if(temperatura == (temperaturaMinima + 1)
                        || temperatura == (temperaturaMaxima - 1)) {
                    log.info("Temperatura próxima do limite aceitável: " + String.format("%.1f", temperatura) + '°');
                }else{
                    log.info("Temperatura aceitavel: " + String.format("%.1f", temperatura) + '°');
                }

                try {
                    sendLeitura(
                            idCamara,
                            new LeituraCamara(
                                    temperatura,
                                    latitudeCT,
                                    longitudeCT
                            )
                    );
                    Thread.sleep(sleepTimeMS);
                } catch (IOException | InterruptedException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    public void sendLeitura(Long idCamara, LeituraCamara leituraCamara) throws IOException, InterruptedException {
        // Montagem da requisição
        var request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/v1/leituras/camara/" + idCamara))
                .header("content-type", "application/json")
                .POST(
                    HttpRequest.BodyPublishers.ofString(
                            mapper.writeValueAsString(leituraCamara)
                    )
                )
                .build();

        client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
    }
}
