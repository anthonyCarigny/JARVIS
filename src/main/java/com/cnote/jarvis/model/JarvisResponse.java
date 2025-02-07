package com.cnote.jarvis.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JarvisResponse {
    private Object data;
    private String message;
    private String speech;
}
