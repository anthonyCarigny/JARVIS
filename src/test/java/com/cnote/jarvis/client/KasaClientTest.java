package com.cnote.jarvis.client;

import com.cnote.jarvis.service.device.client.KasaClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;

import static org.mockserver.model.StringBody.subString;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {"kasa.url=http://localhost:8089"})
public class KasaClientTest {

  @LocalServerPort int randomPort;
  private static ClientAndServer mockServer;
  @Autowired
  KasaClient kasaClient;

  @BeforeEach
  void beforeEach() {
    RestAssured.port = randomPort;
    mockServer = ClientAndServer.startClientAndServer(8089);
  }

  @AfterAll
  static void afterAll() {
    mockServer.stop();
  }

  @Test
  public void shouldLoginWithTheRightParameters() throws JsonProcessingException {
    mockServer
        .when(HttpRequest.request().withMethod(HttpMethod.POST.name()))
        .respond(
            HttpResponse.response(
                    "{\n"
                        + "    \"error_code\": 0,\n"
                        + "    \"result\": {\n"
                        + "        \"accountId\": \"7916597\",\n"
                        + "        \"regTime\": \"2019-11-06 21:19:06\",\n"
                        + "        \"email\": \"anthony.carigny@gmail.com\",\n"
                        + "        \"token\": \"49bb4438-B7WDpGHKGvgGvne6wK55MB0\"\n"
                        + "    }\n"
                        + "}")
                .withContentType(MediaType.APPLICATION_JSON));
    kasaClient.getToken().block();
    mockServer.verify(
        HttpRequest.request()
            .withMethod(HttpMethod.POST.name())
            .withBody(subString("\"method\":\"login\",")));
  }

  @Test
  public void shouldPostPassThroughWithTheRightParameters() throws JsonProcessingException {
    mockServer
        .when(HttpRequest.request().withMethod(HttpMethod.POST.name()))
        .respond(
            HttpResponse.response(
                    "{\n"
                        + "    \"error_code\": 0,\n"
                        + "    \"result\": {\n"
                        + "        \"accountId\": \"7916597\",\n"
                        + "        \"regTime\": \"2019-11-06 21:19:06\",\n"
                        + "        \"email\": \"anthony.carigny@gmail.com\",\n"
                        + "        \"token\": \"49bb4438-B7WDpGHKGvgGvne6wK55MB0\"\n"
                        + "    }\n"
                        + "}")
                .withContentType(MediaType.APPLICATION_JSON));
    kasaClient.turnOnLight().block();
    mockServer.verify(
        HttpRequest.request()
            .withMethod(HttpMethod.POST.name())
            .withBody(subString("\"method\":\"login\",")));
  }
}
