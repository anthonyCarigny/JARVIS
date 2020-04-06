package com.cnote.jarvis.service.device;

import com.cnote.jarvis.service.device.client.KasaClient;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class DeviceManager {

  private KasaClient kasaClient;
  private ModelMapper modelMapper;

  public DeviceManager(@Autowired KasaClient kasaClient) {
    this.kasaClient = kasaClient;
    this.modelMapper = new ModelMapper();
  }

  public Flux<Boolean> turnOnAllLights() {
    return kasaClient.turnOnEverything();
  }
  public Flux<Boolean> turnOffAllLights() {
    return kasaClient.turnOffEverything();
  }
}
