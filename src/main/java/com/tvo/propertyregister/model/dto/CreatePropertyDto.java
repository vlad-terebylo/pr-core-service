package com.tvo.propertyregister.model.dto;

import com.tvo.propertyregister.model.property.PropertyCondition;
import com.tvo.propertyregister.model.property.PropertyType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreatePropertyDto(
        PropertyType propertyType,
        String city,
        String address,
        int square,
        int numberOfRooms,
        BigDecimal cost,
        LocalDate dateOfBecomingOwner,
        LocalDate dateOfBuilding,
        PropertyCondition propertyCondition
) {
}
