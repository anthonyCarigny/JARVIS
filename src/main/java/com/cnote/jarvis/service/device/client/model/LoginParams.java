package com.cnote.jarvis.service.device.client.model;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class LoginParams implements KasaParams {
    private String appType;
    private String cloudUserName;
    private String cloudPassword;
    private String terminalUUID;

    public LoginParams(@Value("${kasa.app_type}") String appType,
                       @Value("${kasa.user}") String username,
                       @Value("${kasa.pass}") String password,
                       @Value("${kasa.terminal}") String terminal){

        this.appType = appType;
        this.cloudUserName = username;
        this.cloudPassword = password;
        this.terminalUUID = terminal;

    }
}
