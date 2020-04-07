package com.cnote.jarvis.client;

import com.cnote.jarvis.service.device.client.KasaClient;
import com.cnote.jarvis.service.device.client.model.KasaDevice;
import org.junit.jupiter.api.*;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockserver.model.StringBody.subString;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {"kasa.url=http://localhost:8089"})
public class KasaClientTest {

  @Autowired KasaClient kasaClient;
  private static ClientAndServer mockServer;
  private static String TOKEN = "49bb4438-B7WDpGHKGvgGvne6wK55MB1";

  @BeforeAll
  static void beforeAll() {
    mockServer = ClientAndServer.startClientAndServer(8089);
  }

  @AfterEach
  void afterEach() {
    mockServer.reset();
  }

  @AfterAll
  static void afterAll() {
    mockServer.stop();
  }

  @Test
  public void login() {
    mockKasaLoginResponse();
    String token = kasaClient.getToken().block();
    mockServer.verify(
        HttpRequest.request()
            .withMethod(HttpMethod.POST.name())
            .withBody(subString("\"method\":\"login\",")));
    assertEquals(TOKEN, token);
  }

  @Test
  public void getDevice() {
    mockKasaLoginResponse();
    mockKasaGetDeviceListResponse();
    Mono<List<KasaDevice>> deviceList = kasaClient.getDevices().collectList();
    List<KasaDevice> devices = deviceList.block();
    mockServer.verify(
            HttpRequest.request(),
            HttpRequest.request());
    Assert.notEmpty(devices, "should have at least one device");
  }

  @Test
  public void passThroughSuccess() {
    mockKasaLoginResponse();
    mockKasaGetDeviceListResponse();
    mockKasaPassThroughResponse();
    Boolean turnedOn = kasaClient.turnOnEverything().blockFirst();
    mockServer.verify(
            HttpRequest.request(),
            HttpRequest.request(),
            HttpRequest.request()
                    .withMethod(HttpMethod.POST.name())
                    .withBody(subString("\"method\":\"passthrough\"")));
    assertEquals(Boolean.TRUE, turnedOn);
  }
  @Test
  public void passThroughFailure() {
    mockKasaLoginResponse();
    mockKasaGetDeviceListResponse();
    mockKasaPassThroughError();
    Boolean turnedOn = kasaClient.turnOnEverything().blockFirst();
    mockServer.verify(
            HttpRequest.request(),
            HttpRequest.request(),
            HttpRequest.request()
                    .withMethod(HttpMethod.POST.name())
                    .withBody(subString("\"method\":\"passthrough\"")));
    assertEquals(Boolean.FALSE, turnedOn);
  }

  private void mockKasaLoginResponse() {
    mockServer
        .when(
            HttpRequest.request()
                .withMethod(HttpMethod.POST.name())
                .withBody(subString("\"method\":\"login\",")))
        .respond(
            HttpResponse.response(
                    "{\n"
                        + "    \"error_code\": 0,\n"
                        + "    \"result\": {\n"
                        + "        \"accountId\": \"1234567\",\n"
                        + "        \"regTime\": \"2019-11-06 21:19:06\",\n"
                        + "        \"email\": \"email@example.com\",\n"
                        + "        \"token\": \""
                        + TOKEN
                        + "\"\n"
                        + "    }\n"
                        + "}")
                .withContentType(MediaType.APPLICATION_JSON));
  }

  private void mockKasaGetDeviceListResponse() {
    mockServer
        .when(
            HttpRequest.request()
                .withMethod(HttpMethod.POST.name())
                .withBody(subString("\"method\":\"getDeviceList\"")))
        .respond(
            HttpResponse.response(
                    "{\n"
                        + "    \"error_code\": 0,\n"
                        + "    \"result\": {\n"
                        + "        \"deviceList\": [\n"
                        + "            {\n"
                        + "                \"deviceType\": \"IOT.SMARTPLUGSWITCH\",\n"
                        + "                \"role\": 0,\n"
                        + "                \"fwVer\": \"1.5.8 Build 180815 Rel.135935\",\n"
                        + "                \"appServerUrl\": \"http://localhost:8089\",\n"
                        + "                \"deviceRegion\": \"eu-west-1\",\n"
                        + "                \"deviceId\": \"azertyuiopqsdfghjklmwxcvbn123456789\",\n"
                        + "                \"deviceName\": \"Smart Wi-Fi Plug\",\n"
                        + "                \"deviceHwVer\": \"2.1\",\n"
                        + "                \"alias\": \"Living Room Lamp\",\n"
                        + "                \"deviceMac\": \"B0BE76D8AD5D\",\n"
                        + "                \"oemId\": \"987654321NBVCXWMLKJHGFDSQPOIUYTREZA\",\n"
                        + "                \"deviceModel\": \"HS100(UK)\",\n"
                        + "                \"hwId\": \"AZERTYUIOPQSDFGHJKLMXCVBN123456789\",\n"
                        + "                \"fwId\": \"00000000000000000000000000000000\",\n"
                        + "                \"isSameRegion\": true,\n"
                        + "                \"status\": 1\n"
                        + "            }\n"
                        + "        ]\n"
                        + "    }\n"
                        + "}")
                .withContentType(MediaType.APPLICATION_JSON)
        );
  }

  private void mockKasaPassThroughResponse() {
    mockServer
            .when(
                    HttpRequest.request()
                            .withMethod(HttpMethod.POST.name())
                            .withQueryStringParameter("token", TOKEN)
                            .withBody(subString("\"method\":\"passthrough\"")))
            .respond(
                    HttpResponse.response(
                            "{\"error_code\":0,\n"
                                    + "  \"result\": {\n"
                                    + "    \"responseData\": \"ok\""
                                    + "  }\n"
                                    + "}")
                            .withContentType(MediaType.APPLICATION_JSON));
  }
  private void mockKasaPassThroughError() {
    mockServer
        .when(
            HttpRequest.request()
                .withMethod(HttpMethod.POST.name())
                .withQueryStringParameter("token", TOKEN)
                .withBody(subString("\"method\":\"passthrough\"")))
        .respond(
            HttpResponse.response(
                    "{\n"
                        + "    \"error_code\": -20651,\n"
                        + "    \"msg\": \"Token expired\"\n"
                        + "}")
                .withContentType(MediaType.APPLICATION_JSON));
  }

}
