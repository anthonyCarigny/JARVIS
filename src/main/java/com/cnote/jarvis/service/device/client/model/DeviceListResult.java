package com.cnote.jarvis.service.device.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceListResult {
    private ArrayList<Device> deviceList;
}
