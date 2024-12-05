package com.tvo.propertyregister.repository.inmemory;

import com.tvo.propertyregister.model.TaxRate;
import com.tvo.propertyregister.repository.TaxRateRepository;

import java.util.List;

public class InMemoryTaxRateRealization implements TaxRateRepository {
    @Override
    public List<TaxRate> getAll() {
        return null;
    }

    @Override
    public void changeTax(String propertyType, TaxRate taxRate) {

    }
}
