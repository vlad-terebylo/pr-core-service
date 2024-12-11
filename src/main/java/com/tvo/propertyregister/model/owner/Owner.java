package com.tvo.propertyregister.model.owner;

import com.tvo.propertyregister.model.property.Property;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Owner {

    private int id;
    private String firstName;
    private String lastName;
    private int age;
    private FamilyStatus familyStatus;

    @Getter
    private boolean hasChildren;
    private String email;
    private String phoneNumber;
    private LocalDate birthday;
    private float taxesDept;
    private List<Property> properties;

}
