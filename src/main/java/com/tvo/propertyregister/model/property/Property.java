package com.tvo.propertyregister.model.property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Property {

    private int id;
    private PropertyType propertyType;
    private String city;
    private String address;
    private int square;
    private int numberOfRooms;
    private float taxesForProperty;
    private float cost;
    private LocalDate dateOfBecomingOwner;
    private LocalDate dateOfBuilding;
    private PropertyCondition propertyCondition;
}
