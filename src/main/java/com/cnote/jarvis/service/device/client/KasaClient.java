package com.cnote.jarvis.service.device.client;

import com.cnote.jarvis.service.device.client.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Slf4j
@Component
public class KasaClient {
  private String url;
  private WebClient client;
  @Autowired LoginParams loginParams;
  @Autowired PassThroughParams passThroughParams;

  public KasaClient(@Value("${kasa.url}") String url) {
    this.url = url;
    client = WebClient.create(url);
  }

  @Scheduled(cron = "0 0 0 0 * ?")
  public Mono<String> getToken() {
    log.debug("URL : {}", url);
    KasaRequest kasaRequest = new KasaRequest("login", loginParams);

    return client
        .post()
        .bodyValue(kasaRequest)
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .exchange()
        .flatMap(clientResponse -> clientResponse.bodyToMono(LoginResponse.class))
        .doOnSuccess(loginResponse -> log.info(loginResponse.toString()))
        .flatMap(loginResponse -> Mono.just(loginResponse.getResult().getToken()));
  }

  public Mono<String> turnOnLight() {
    return passThrough();
  }

  public Mono<ArrayList<Device>> getDevices() {
    KasaRequest kasaRequest = new KasaRequest("getDeviceList", null);
    log.debug(kasaRequest.toString());
    return getToken()
        .flatMap(
            token ->
                client
                    .post()
                    .attribute("token", token)
                    .bodyValue(kasaRequest)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .exchange()
                    .flatMap(clientResponse -> clientResponse.bodyToMono(DeviceListResponse.class))
                    .doOnSuccess(deviceListResponse -> log.info(deviceListResponse.toString()))
                    .flatMap(
                        deviceListResponse ->
                            Mono.just(deviceListResponse.getResult().getDeviceList())));
  }

  private Mono<String> passThrough() {
    KasaRequest kasaRequest = new KasaRequest("passthrough", passThroughParams);
    log.debug(kasaRequest.toString());
    return getToken()
        .flatMap(
            token ->
                client
                    .post()
                    .attribute("token", token)
                    .bodyValue(kasaRequest)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .exchange()
                    .flatMap(clientResponse -> clientResponse.bodyToMono(PassThroughResponse.class))
                    .doOnSuccess(passThroughResponse -> log.info(passThroughResponse.toString()))
                    .flatMap(
                        passThroughResponse -> Mono.just(passThroughResponse.getError_code())));
  }
}
