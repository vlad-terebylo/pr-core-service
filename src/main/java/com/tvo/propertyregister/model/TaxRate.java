package com.tvo.propertyregister.model;

import com.tvo.propertyregister.model.property.PropertyType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaxRate {

    private int id;
    private PropertyType propertyType;
    private float tax;
}
