package com.cnote.jarvis.service.device.client.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PassThroughParams implements KasaParams {
    private String deviceId;
    private String requestData;
}
