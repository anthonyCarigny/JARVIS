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

  public KasaClient(@Value("${kasa.url}") String url) {
    this.url = url;
    HttpClient httpClient = HttpClient.create().wiretap(true);
    this.client =
        WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .baseUrl(url)
            .build();
  }

  @Scheduled(cron = "0 0 0 0 * ?")
  @Cacheable
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


  @Cacheable
  public Flux<Device> getDevices() {
    KasaRequest kasaRequest = KasaRequest.builder().method("getDeviceList").build();
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
                    .doOnSuccess(deviceListResponse -> log.info(deviceListResponse.toString()))
                    .flatMap(deviceListResponse -> Mono.justOrEmpty(deviceListResponse.getResult()))
                    .filter(Objects::nonNull)
                    .flatMapIterable(deviceListResult -> deviceListResult.getDeviceList()));
  }

  private Mono<Integer> passThrough(Device device, int state) {
    PassThroughParams passThroughParams =
        new PassThroughParams(
            device.getDeviceId(), "{\"system\":{\"set_relay_state\":{\"state\":"+state+"}}}");
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
                    .flatMap(
                        passThroughResponse -> Mono.just(passThroughResponse.getError_code())));
  }

  public Flux<Boolean> turnOnEverything() {
    return getDevices()
            .flatMap(device -> turnOnDevice(device))
            .flatMap(
                    error_code -> {
                      if (error_code == 0) {
                        return Mono.just(true);
                      }
                      return Mono.just(false);
                    });
  }
  public Flux<Boolean> turnOffEverything() {
    return getDevices()
            .flatMap(device -> turnOffDevice(device))
            .flatMap(
                    error_code -> {
                      if (error_code == 0) {
                        return Mono.just(true);
                      }
                      return Mono.just(false);
                    });
  }

  private Mono<Integer> turnOnDevice(Device device) {
    return passThrough(device,1);
  }
  private Mono<Integer> turnOffDevice(Device device) {
    return passThrough(device,0);
  }
}
