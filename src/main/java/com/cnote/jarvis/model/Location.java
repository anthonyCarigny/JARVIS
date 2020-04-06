package com.cnote.jarvis.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class Location {
    @NonNull
    private String locationName;
}
