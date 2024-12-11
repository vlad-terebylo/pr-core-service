package com.tvo.propertyregister.repository;

import com.tvo.propertyregister.model.TaxRate;
import com.tvo.propertyregister.model.property.PropertyType;

import java.util.List;

public interface TaxRateRepository {
    List<TaxRate> getAll();

    void changeTax(PropertyType propertyType, TaxRate taxRate);
}
