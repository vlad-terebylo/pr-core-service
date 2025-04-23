package com.tvo.propertyregister.integration.config.repository;

import com.mongodb.client.result.UpdateResult;
import com.tvo.propertyregister.model.TaxRate;
import com.tvo.propertyregister.model.property.PropertyType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class TaxRateTestRepository {
    private static final String TAX_RATE_COLLECTION = "taxRates";

    private final MongoTemplate mongoTemplate;

    public List<TaxRate> findAll() {
        Query criteria = new Query();

        return mongoTemplate.find(criteria, TaxRate.class, TAX_RATE_COLLECTION);
    }

    public void initTaxRates() {
        TaxRate flatRate = new TaxRate(1, PropertyType.FLAT, new BigDecimal("6"));
        TaxRate houseRate = new TaxRate(2, PropertyType.HOUSE, new BigDecimal("8"));
        TaxRate officeRate = new TaxRate(3, PropertyType.OFFICE, new BigDecimal("13"));

        mongoTemplate.insert(flatRate, TAX_RATE_COLLECTION);
        mongoTemplate.insert(houseRate, TAX_RATE_COLLECTION);
        mongoTemplate.insert(officeRate, TAX_RATE_COLLECTION);
    }

    public void insertTaxRate(TaxRate newRate) {
        if (Objects.isNull(newRate)) {
            throw new RuntimeException("New rate is null");
        }

        mongoTemplate.insert(newRate, TAX_RATE_COLLECTION);
    }

    public void clear() {
        mongoTemplate.remove(new Query(), TAX_RATE_COLLECTION);
    }
}
