package com.cnote.jarvis.service.device.client.model;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class PassThroughParams implements KasaParams {
    private String deviceId;
    private String requestData;

    public PassThroughParams(@Value("${kasa.deviceId}") String deviceId,
                             @Value("${kasa.requestData}") String requestData){
        this.deviceId = deviceId;
        this.requestData = requestData;
    }
}
