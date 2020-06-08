package com.cnote.jarvis.service.device.client;

import com.cnote.jarvis.service.device.client.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.Objects;

@Slf4j
@Component
public class KasaClient {
  private String url;
  private WebClient client;
  @Autowired LoginParams loginParams;

  public KasaClient(@Value("${kasa.url}") String url, WebClient.Builder webclientBuilder) {
    this.url = url;
    HttpClient httpClient = HttpClient.create().wiretap(true);
    this.client = webclientBuilder
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .baseUrl(url)
            .build();
  }

  @Scheduled(cron = "0 0 0 0 * ?")
  @Cacheable("token")
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
        .map(loginResponse -> loginResponse.getResult().getToken());
  }

  @Cacheable("devices")
  public Flux<KasaDevice> getDevices() {
    KasaRequestWithoutParams kasaRequest = new KasaRequestWithoutParams("getDeviceList");
    log.info(kasaRequest.toString());
    return getToken()
        .flatMapMany(
            token ->
                client
                    .post()
                    .uri(uriBuilder -> uriBuilder.queryParam("token", token).build())
                    .bodyValue(kasaRequest)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .exchange()
                    .flatMap(clientResponse -> clientResponse.bodyToMono(DeviceListResponse.class))
                    .map(DeviceListResponse::getResult)
                    .filter(Objects::nonNull)
                    .flatMapIterable(DeviceListResult::getDeviceList));
  }

  private Mono<Integer> passThrough(KasaDevice device, int state) {
    PassThroughParams passThroughParams =
        new PassThroughParams(
            device.getDeviceId(), "{\"system\":{\"set_relay_state\":{\"state\":" + state + "}}}");
    KasaRequest kasaRequest = new KasaRequest("passthrough", passThroughParams);
    log.debug(kasaRequest.toString());
    HttpClient httpClient = HttpClient.create().wiretap(true);
    WebClient deviceClient =
        WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .baseUrl(device.getAppServerUrl())
            .build();
    return getToken()
        .flatMap(
            token ->
                deviceClient
                    .post()
                    .uri(uriBuilder -> uriBuilder.queryParam("token", token).build())
                    .bodyValue(kasaRequest)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .exchange()
                    .flatMap(clientResponse -> clientResponse.bodyToMono(PassThroughResponse.class))
                    .doOnSuccess(passThroughResponse -> log.debug(passThroughResponse.toString()))
                    .map(KasaResponse::getError_code));
  }

  public Flux<Boolean> turnOnEverything() {
    return getDevices()
        .flatMap(this::turnOnDevice)
        .map(error_code -> error_code == 0);
  }

  public Flux<Boolean> turnOffEverything() {
    return getDevices()
        .flatMap(this::turnOffDevice)
        .map(error_code -> error_code == 0);
  }

  private Mono<Integer> turnOnDevice(KasaDevice device) {
    return passThrough(device, 1);
  }

  private Mono<Integer> turnOffDevice(KasaDevice device) {
    return passThrough(device, 0);
  }
}
