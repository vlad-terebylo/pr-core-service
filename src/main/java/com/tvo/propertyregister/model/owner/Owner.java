package com.tvo.propertyregister.model.owner;

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
    private BigDecimal taxesDept;
    private List<Property> properties;

    public Owner(int id, String email, String firstName, String lastName, BigDecimal taxesDept, boolean hasChildren, FamilyStatus familyStatus) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.taxesDept = taxesDept;
        this.hasChildren = hasChildren;
        this.familyStatus = familyStatus;
    }
}