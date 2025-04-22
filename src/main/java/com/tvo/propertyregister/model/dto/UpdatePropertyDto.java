package com.tvo.propertyregister.model.dto;

import com.tvo.propertyregister.model.property.PropertyCondition;

public record UpdatePropertyDto(
        String city,
        String address,
        int numberOfRooms,
        PropertyCondition propertyCondition

) {
}
