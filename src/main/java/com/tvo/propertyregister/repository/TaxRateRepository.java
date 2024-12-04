package com.tvo.propertyregister.repository;

import com.tvo.propertyregister.model.TaxRate;

import java.util.List;

public interface TaxRateRepository {
    List<TaxRate> getAll();
}
