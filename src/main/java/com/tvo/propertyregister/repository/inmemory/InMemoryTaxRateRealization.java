package com.tvo.propertyregister.repository.inmemory;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.tvo.propertyregister.model.TaxRate;
import com.tvo.propertyregister.model.property.PropertyType;
import com.tvo.propertyregister.repository.TaxRateRepository;

import java.util.List;

public class InMemoryTaxRateRealization implements TaxRateRepository {

    List<TaxRate> taxRates = List.of(
            new TaxRate(1, PropertyType.FLAT, 6),
            new TaxRate(2, PropertyType.HOUSE, 8),
            new TaxRate(3, PropertyType.OFFICE, 13));

    @Override
    public List<TaxRate> getAll() {
        return this.taxRates;
    }

    @Override
    public void changeTax(PropertyType propertyType, TaxRate taxRate) {
        for(TaxRate currentTaxRate: this.taxRates){
            if(propertyType == currentTaxRate.getPropertyType()){
                currentTaxRate.setTax(taxRate.getTax());
            }
        }
    }
}
