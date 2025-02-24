package com.tvo.propertyregister.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public record EmailEventDto(
        String email,
        String firstName,
        String lastName,
        BigDecimal debt,
        EmailType type) implements Serializable {
}
