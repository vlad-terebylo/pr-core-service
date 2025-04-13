package com.tvo.propertyregister.model.owner;

import com.tvo.propertyregister.model.dto.CreateOwnerDto;
import com.tvo.propertyregister.model.dto.UpdateOwnerDto;
import com.tvo.propertyregister.model.property.Property;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@With
@NoArgsConstructor
@AllArgsConstructor
public class Owner {
    private int id;
    private String firstName;
    private String lastName;
    private int age;
    private FamilyStatus familyStatus;
    private boolean hasChildren;
    private String email;
    private String phoneNumber;
    private LocalDate birthday;
    private BigDecimal taxesDebt;
    private List<Property> properties;

    public Owner(int id, String email, String firstName, String lastName, BigDecimal taxesDebt, boolean hasChildren, FamilyStatus familyStatus) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.taxesDebt = taxesDebt;
        this.hasChildren = hasChildren;
        this.familyStatus = familyStatus;
    }

    public Owner(CreateOwnerDto ownerDto) {
        this.firstName = ownerDto.firstName();
        this.lastName = ownerDto.lastName();
        this.age = ownerDto.age();
        this.familyStatus = ownerDto.familyStatus();
        this.hasChildren = ownerDto.hasChildren();
        this.email = ownerDto.email();
        this.phoneNumber = ownerDto.phoneNumber();
        this.birthday = ownerDto.birthday();
        this.taxesDebt = ownerDto.taxesDebt();
    }

    public Owner(UpdateOwnerDto ownerDto) {
        this.firstName = ownerDto.firstName();
        this.lastName = ownerDto.lastName();
        this.age = ownerDto.age();
        this.familyStatus = ownerDto.familyStatus();
        this.hasChildren = ownerDto.hasChildren();
        this.email = ownerDto.email();
        this.phoneNumber = ownerDto.phoneNumber();
        this.birthday = ownerDto.birthday();
        this.taxesDebt = ownerDto.taxesDebt();
    }
}