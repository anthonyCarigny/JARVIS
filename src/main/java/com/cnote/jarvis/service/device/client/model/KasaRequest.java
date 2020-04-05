package com.cnote.jarvis.service.device.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class KasaRequest {
    private String method;
    private KasaParams params;
}
