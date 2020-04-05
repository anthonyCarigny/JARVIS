package com.cnote.jarvis.service.device.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Device {
    private String deviceType;
    private String role;
    private String fwVer;
    private String appServerUrl;
    private String deviceRegion;
    private String deviceId;
    private String deviceName;
    private String deviceHwVer;
    private String alias;
    private String deviceMac;
    private String oemId;
    private String deviceModel;
    private String hwId;
    private String fwId;
    private String isSameRegion;
    private String status;
}
