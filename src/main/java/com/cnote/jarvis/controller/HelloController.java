package com.cnote.jarvis.controller;

import com.cnote.jarvis.model.JarvisResponse;
import com.cnote.jarvis.model.Location;
import com.cnote.jarvis.service.device.DeviceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Slf4j
@RestController
public class HelloController {

  private static final String MORNING_GREETING = "Good morning, %s !";
  private static final String AFTERNOON_GREETING = "Good afternoon, %s!";
  private static final String EVENING_GREETING = "Good evening, %s !";
  private static final String NIGHT_GREETING = "I wasn't sleeping either, %s !";
  private static final String HOME = "home";

  private DeviceManager deviceManager;

  public HelloController(@Autowired DeviceManager deviceManager) {
    this.deviceManager = deviceManager;
  }

  @GetMapping("/")
  @ResponseBody
  public JarvisResponse introduction() {
    return new JarvisResponse(
        null,
        "Allow me to introduce myself. I am J.A.R.V.I.S. A virtual artificial intelligence, and I am here to assist you with a variety of tasks as best I can 24 hours a day, seven days a week.",
        "caged_intro_2");
  }

  @GetMapping({"/hello"})
  @ResponseBody
  public JarvisResponse sayHello() {
    if (LocalDateTime.now().getHour() <= 4) {
      return new JarvisResponse(
          LocalDateTime.now().toString(),
          String.format(NIGHT_GREETING, "sir"),
          "caged_clock_late_0");
    } else if (LocalDateTime.now().getHour() <= 12) {
      return new JarvisResponse(
          LocalDateTime.now().toString(),
          String.format(MORNING_GREETING, "sir"),
          "caged_listening_on_morning");
    } else if (LocalDateTime.now().getHour() <= 18) {
      return new JarvisResponse(
              LocalDateTime.now().toString(),
              String.format(AFTERNOON_GREETING, "sir"),
              "caged_listening_on_afternoon");
    } else {
      return new JarvisResponse(
              LocalDateTime.now().toString(),
              String.format(EVENING_GREETING, "sir"),
              "caged_listening_on_evening");
    }
  }

  @PostMapping(path = "/checkIn", consumes = "application/json", produces = "application/json")
  @ResponseBody
  public Mono<JarvisResponse> checkIn(@RequestBody Location location) {
    JarvisResponse response = new JarvisResponse(null, "", "caged_trs_help");
    if (HOME.contentEquals(location.getLocationName())) {
      return deviceManager
          .turnOnAllLights()
          .collectList()
          .defaultIfEmpty(List.of(false))
          .map(
              devices -> {
                log.info("devices = " + devices);
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
  public Mono<JarvisResponse> checkOut(@RequestBody Location location) {
    JarvisResponse response = new JarvisResponse(null, "", "caged_sleep_2");
    if (HOME.contentEquals(location.getLocationName())) {
      return deviceManager
          .turnOffAllLights()
          .collectList()
          .defaultIfEmpty(List.of(false))
              .map(
                      devices -> {
                        log.info("devices = " + devices);
                        String message =
                                devices.contains(Boolean.FALSE)
                                        ? "I couldn't turn off all connected devices for you."
                                        : "I turned off all connected devices for you.";
                        response.setMessage(message);
                        return devices;
                      })
              .thenReturn(response);
    } else {
      response.setMessage(String.format("I see you left %s", location.getLocationName()));
      return Mono.just(response);
    }
  }
  
  public JarvisResponse returnRandomGreeting() {
    JarvisResponse jarvisResponse1 = new JarvisResponse(
            null,
            String.format("welcome back %s! How may I be of assistance ?", "sir"),
            "caged_listening_on_1");

    List<JarvisResponse> givenList = Collections.singletonList(jarvisResponse1);
    Random rand = new Random();
    return givenList.get(rand.nextInt(givenList.size()));
  }
}
