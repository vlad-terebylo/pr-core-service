package com.tvo.propertyregister.model.property;

import com.tvo.propertyregister.model.dto.CreatePropertyDto;
import com.tvo.propertyregister.model.dto.UpdatePropertyDto;
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

    public Property(CreatePropertyDto createPropertyDto) {
        this.propertyType = createPropertyDto.propertyType();
        this.city = createPropertyDto.city();
        this.address = createPropertyDto.address();
        this.square = createPropertyDto.square();
        this.numberOfRooms = createPropertyDto.numberOfRooms();
        this.cost = createPropertyDto.cost();
        this.dateOfBecomingOwner = createPropertyDto.dateOfBecomingOwner();
        this.dateOfBuilding = createPropertyDto.dateOfBuilding();
        this.propertyCondition = createPropertyDto.propertyCondition();
    }

    public Property(UpdatePropertyDto updatePropertyDto) {
        this.city = updatePropertyDto.city();
        this.address = updatePropertyDto.address();
        this.numberOfRooms = updatePropertyDto.numberOfRooms();
        this.propertyCondition = updatePropertyDto.propertyCondition();
    }
}