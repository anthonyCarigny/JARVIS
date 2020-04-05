package com.cnote.jarvis.controller;

import com.cnote.jarvis.service.device.client.KasaClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class HelloController {

    private static final String template = "Hello, %s!";
    private static final String HOME = "home";
    private final AtomicLong counter = new AtomicLong();

    @Autowired
    private KasaClient kasaClient;


    @GetMapping({"/", "/hello"})
    @ResponseBody
    public JarvisResponse sayHello(@RequestParam(name="name", required=false, defaultValue="Sir") String name) {
        return new JarvisResponse(null, "", String.format(template, name));
    }

    @PostMapping("/checkIn/{locationName}") //TODO should be a body parameter
    public Mono<String> checkIn(@PathVariable String locationName) throws JsonProcessingException {
        if(HOME.contentEquals(locationName)){
            return kasaClient.turnOnLight().thenReturn("welcome home sir");
        }
        return Mono.just(String.format("I see you arrived %", locationName));
    }

}
