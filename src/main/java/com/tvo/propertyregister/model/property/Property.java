package com.tvo.propertyregister.model.property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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
    private BigDecimal cost;
    private LocalDate dateOfBecomingOwner;
    private LocalDate dateOfBuilding;
    private PropertyCondition propertyCondition;
}