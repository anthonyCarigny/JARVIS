package com.cnote.jarvis.controller;

import com.cnote.jarvis.model.Location;
import com.cnote.jarvis.service.device.DeviceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
public class HelloController {

  private static final String template = "Hello, %s!";
  private static final String HOME = "home";

  @Autowired private DeviceManager deviceManager;

  @GetMapping({"/", "/hello"})
  @ResponseBody
  public JarvisResponse sayHello(
      @RequestParam(name = "name", required = false, defaultValue = "Sir") String name) {
    return new JarvisResponse(null, "", String.format(template, name));
  }

  @PostMapping(path = "/checkIn", consumes = "application/json", produces = "application/json")
  public Mono<String> checkIn(@RequestBody Location location) {
    if (HOME.contentEquals(location.getLocationName())) {
      return deviceManager
              .turnOnAllLights()
//          .flatMap(
//              turnedOn -> {
//                if (turnedOn == false) {
//                  Flux.error(new Error("could not turn on all the lights"));
//                }
//                return true;
//              })
              .then(Mono.just("Welcome home sir"));
    }
    return Mono.just(String.format("I see you arrived %s", location.getLocationName()));
  }
  @PostMapping(path = "/checkOut", consumes = "application/json", produces = "application/json")
  public Mono<String> checkOut(@RequestBody Location location) {
    if (HOME.contentEquals(location.getLocationName())) {
      return deviceManager
              .turnOffAllLights()
              .then(Mono.just("I turned off all lights for you sir."));
    }
    return Mono.just(String.format("I see you left %s", location.getLocationName()));
  }
}
