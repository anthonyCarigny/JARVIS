package com.cnote.jarvis.controller;

import com.cnote.jarvis.model.Location;
import com.cnote.jarvis.service.device.DeviceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class HelloController {

  private static final String templateMorning = "Good morning, %s!";
  private static final String templateAfternoon = "Good afternoon, %s!";
  private static final String templateEvening = "Good evening, %s!";
  private static final String templateNight = "I wasn't sleeping either %s!";
  private static final String HOME = "home";

  @Autowired private DeviceManager deviceManager;

  @GetMapping({"/", "/hello"})
  @ResponseBody
  public JarvisResponse sayHello(
      @RequestParam(name = "name", required = false, defaultValue = "Sir") String name) {
    if(LocalDateTime.now().getHour()<=4 ){
      return new JarvisResponse(null, LocalDateTime.now().toString(), String.format(templateNight, name));
    }
    else if(LocalDateTime.now().getHour()<=12 ){
      return new JarvisResponse(null, LocalDateTime.now().toString(), String.format(templateMorning, name));
    }
    else if(LocalDateTime.now().getHour()<=18 ){
      return new JarvisResponse(null, LocalDateTime.now().toString(), String.format(templateAfternoon, name));
    }
    else {
      return new JarvisResponse(null, LocalDateTime.now().toString(), String.format(templateEvening, name));
    }
  }

  @PostMapping(path = "/checkIn", consumes = "application/json", produces = "application/json")
  @ResponseBody
  public Mono<JarvisResponse> checkIn(@RequestBody Location location) {
    JarvisResponse response = new JarvisResponse(null, "", "");
    if (HOME.contentEquals(location.getLocationName())) {
      return deviceManager
          .turnOnAllLights()
          .collectList()
          .defaultIfEmpty(List.of(false))
          .map(
              devices -> {
                System.out.println("devices = " + devices);
                String message =
                    devices.contains(Boolean.FALSE)
                        ? "Welcome home sir. It looks like I couldn't reach some devices"
                        : "Welcome home sir";
                response.setMessage(message);
                return devices;
              })
          .thenReturn(response);
    } else {
      response.setMessage(String.format("I see you arrived %s", location.getLocationName()));
      return Mono.just(response);
    }
  }

  @PostMapping(path = "/checkOut", consumes = "application/json", produces = "application/json")
  public Mono<String> checkOut(@RequestBody Location location) {
    if (HOME.contentEquals(location.getLocationName())) {
      return deviceManager
          .turnOffAllLights()
          .collectList()
          .thenReturn("I turned off all lights for you sir.");
    } else {
      return Mono.just(String.format("I see you left %s", location.getLocationName()));
    }
  }
}
