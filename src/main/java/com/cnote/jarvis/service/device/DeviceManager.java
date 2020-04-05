package com.cnote.jarvis.service.device;

import com.cnote.jarvis.service.device.client.KasaClient;
import com.cnote.jarvis.service.device.model.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class DeviceManager {

    private List<Device> devices;
    private KasaClient kasaClient;

    public DeviceManager(@Autowired KasaClient kasaClient){
        this.kasaClient = kasaClient;
    }
    public boolean turnOnAllLights(){
        return true;
    }

}
