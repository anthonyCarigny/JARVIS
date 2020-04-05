package com.cnote.jarvis.service.device.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PassThroughResponse extends KasaResponse {
    private PassThroughResult result;
}
