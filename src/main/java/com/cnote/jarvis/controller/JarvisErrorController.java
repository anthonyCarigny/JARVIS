package com.cnote.jarvis.controller;

import com.cnote.jarvis.model.JarvisResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class JarvisErrorController {

  private static final String PATH = "/notfound";

  @GetMapping(PATH)
  public JarvisResponse notFound() {
    return new JarvisResponse(
            LocalDateTime.now().toString(),"My apologies, this protocol is not yet available.",
            "caged_unavailable_3");
  }

  protected JarvisResponse ResponseStatusException(ResponseStatusException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

    return new JarvisResponse(
            LocalDateTime.now().toString(),"My apologies, this protocol is not yet available.",
            "caged_unavailable_3");
  }

  //other exception handlers below

}