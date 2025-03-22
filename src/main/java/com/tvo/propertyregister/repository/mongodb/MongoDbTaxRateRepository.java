package com.tvo.propertyregister.repository.mongodb;

import com.tvo.propertyregister.model.TaxRate;
import com.tvo.propertyregister.model.property.PropertyType;
import com.tvo.propertyregister.repository.TaxRateRepository;

import java.math.BigDecimal;
import java.util.List;

public class MongoDbTaxRateRepository implements TaxRateRepository {
    @Override
    public List<TaxRate> getAll() {
        return null;
    }

    @Override
    public boolean changeTax(PropertyType propertyType, BigDecimal rate) {
        return true;
    }

}
