package com.tvo.propertyregister.model.dto;

import java.io.Serializable;
import java.util.Map;

public record EmailEventDto(
        String email,
        EmailType type,
        Map<String, String> params) implements Serializable {
}
