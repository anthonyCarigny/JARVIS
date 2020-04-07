package com.cnote.jarvis.service.device;

import com.cnote.jarvis.service.device.client.KasaClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class DeviceManager {

  private KasaClient kasaClient;

  public DeviceManager(@Autowired KasaClient kasaClient) {
    this.kasaClient = kasaClient;
  }

  public Flux<Boolean> turnOnAllLights() {
    return kasaClient.turnOnEverything();
  }
  public Flux<Boolean> turnOffAllLights() {
    return kasaClient.turnOffEverything();
  }
}
