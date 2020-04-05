package com.cnote.jarvis.service.device.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Device {
  private String identifier; //a globally unique identifier for this device
  private String name; // a friendly name for this device
  private String device_type; //the type of the device as understood by the client
  private String controller_gateway; //the address of the device's controller
}
