package com.tvo.propertyregister.repository.mongodb;

import com.tvo.propertyregister.model.TaxRate;
import com.tvo.propertyregister.model.property.PropertyType;
import com.tvo.propertyregister.repository.TaxRateRepository;

import java.util.List;

public class MongoDBTaxRateRealization implements TaxRateRepository {
    @Override
    public List<TaxRate> getAll() {
        return null;
    }

    @Override
    public void changeTax(PropertyType propertyType, TaxRate taxRate) {

    }
}
