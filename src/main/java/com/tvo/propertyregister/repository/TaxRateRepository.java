package com.tvo.propertyregister.repository;

import com.tvo.propertyregister.model.TaxRate;
import com.tvo.propertyregister.model.property.PropertyType;

import java.math.BigDecimal;
import java.util.List;

public interface TaxRateRepository {
    List<TaxRate> findAll();

    boolean changeTax(PropertyType propertyType, BigDecimal rate);
}
