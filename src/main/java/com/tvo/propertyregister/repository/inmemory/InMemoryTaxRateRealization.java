package com.tvo.propertyregister.repository.inmemory;

import com.tvo.propertyregister.model.TaxRate;
import com.tvo.propertyregister.model.property.PropertyType;
import com.tvo.propertyregister.repository.TaxRateRepository;

import java.math.BigDecimal;
import java.util.List;

public class InMemoryTaxRateRealization implements TaxRateRepository {

    private List<TaxRate> taxRates = List.of(
            new TaxRate(1, PropertyType.FLAT, new BigDecimal("6")),
            new TaxRate(2, PropertyType.HOUSE, new BigDecimal("8")),
            new TaxRate(3, PropertyType.OFFICE, new BigDecimal("13")));

    @Override
    public List<TaxRate> getAll() {
        return this.taxRates;
    }

    @Override
    public boolean changeTax(PropertyType propertyType, BigDecimal rate) {
        for (TaxRate currentTaxRate : this.taxRates) {
            if (propertyType == currentTaxRate.getPropertyType()) {
                currentTaxRate.setTax(rate);
                return true;
            }
        }
        return false;
    }
}
