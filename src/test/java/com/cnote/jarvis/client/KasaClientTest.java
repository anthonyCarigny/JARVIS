package com.cnote.jarvis.client;

import com.cnote.jarvis.service.device.client.KasaClient;
import com.cnote.jarvis.service.device.client.model.Device;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import org.mockserver.model.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;

import java.util.ArrayList;

import static org.mockserver.model.StringBody.subString;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {"kasa.url=http://localhost:8089", "device.gateway=http://localhost:8089"})
public class KasaClientTest {

  @LocalServerPort int randomPort;
  private static ClientAndServer mockServer;
  @Autowired KasaClient kasaClient;

  @BeforeEach
  void beforeEach() {
    RestAssured.port = randomPort;
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
  public void shouldLoginWithTheRightParameters() throws JsonProcessingException {
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
  public void shouldPostPassThroughWithTheRightParameters() {
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
                        + "        \"accountId\": \"7916597\",\n"
                        + "        \"regTime\": \"2019-11-06 21:19:06\",\n"
                        + "        \"email\": \"anthony.carigny@gmail.com\",\n"
                        + "        \"token\": \"49bb4438-B7WDpGHKGvgGvne6wK55MB0\"\n"
                        + "    }\n"
                        + "}")
                .withContentType(MediaType.APPLICATION_JSON));

    mockServer
        .when(
            HttpRequest.request()
                .withQueryStringParameter("token", "49bb4438-B7WDpGHKGvgGvne6wK55MB0")
                .withMethod(HttpMethod.POST.name()))
        .respond(
            HttpResponse.response(
                    "{\"error_code\":0,\n"
                        + "  \"result\": {\n"
                        + "    \"responseData\": \"ok\""
                        + "  }\n"
                        + "}")
                .withContentType(MediaType.APPLICATION_JSON));

    kasaClient.turnOnEverything().blockLast();
    mockServer.verify(
        HttpRequest.request()
            .withMethod(HttpMethod.POST.name())
            .withBody(subString("\"method\":\"passthrough\",")));
  }

  @Test
  public void shouldgetDevicesWithTheRightParameters() {
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
                                    + "        \"accountId\": \"7916597\",\n"
                                    + "        \"regTime\": \"2019-11-06 21:19:06\",\n"
                                    + "        \"email\": \"anthony.carigny@gmail.com\",\n"
                                    + "        \"token\": \"49bb4438-B7WDpGHKGvgGvne6wK55MB0\"\n"
                                    + "    }\n"
                                    + "}")
                            .withContentType(MediaType.APPLICATION_JSON));

    mockServer
        .when(HttpRequest.request().withMethod(HttpMethod.POST.name())
                .withBody(subString("\"method\":\"getDeviceList\",")))
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
                        + "                \"appServerUrl\": \"https://eu-wap.tplinkcloud.com\",\n"
                        + "                \"deviceRegion\": \"eu-west-1\",\n"
                        + "                \"deviceId\": \"800633CAADA975DBDF2B5C4CA61913781AE712B2\",\n"
                        + "                \"deviceName\": \"Smart Wi-Fi Plug\",\n"
                        + "                \"deviceHwVer\": \"2.1\",\n"
                        + "                \"alias\": \"Living Room Lamp\",\n"
                        + "                \"deviceMac\": \"B0BE76D8AD5D\",\n"
                        + "                \"oemId\": \"FDD18403D5E8DB3613009C820963E018\",\n"
                        + "                \"deviceModel\": \"HS100(UK)\",\n"
                        + "                \"hwId\": \"82589DCE59161C80EC57E0A2834D25A2\",\n"
                        + "                \"fwId\": \"00000000000000000000000000000000\",\n"
                        + "                \"isSameRegion\": true,\n"
                        + "                \"status\": 1\n"
                        + "            }\n"
                        + "        ]\n"
                        + "    }\n"
                        + "}")
                .withContentType(MediaType.APPLICATION_JSON));
    ArrayList<Device> devices = kasaClient.getDevices().block();
    mockServer.verify(
        HttpRequest.request()
            .withMethod(HttpMethod.POST.name())
            .withBody(subString("\"method\":\"getDeviceList\",")));
    Assert.notEmpty(devices, "should have at least one device");
  }
}
