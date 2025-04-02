package com.tvo.propertyregister.repository.mongodb;

import com.mongodb.client.result.UpdateResult;
import com.tvo.propertyregister.model.TaxRate;
import com.tvo.propertyregister.model.property.PropertyType;
import com.tvo.propertyregister.repository.TaxRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
public class MongoDbTaxRateRepository implements TaxRateRepository {

    private static final String TAX_RATE_COLLECTION = "taxRates";

    private final MongoTemplate mongoTemplate;

    @Override
    public List<TaxRate> findAll() {
        Query criteria = new Query();

        return mongoTemplate.find(criteria, TaxRate.class, TAX_RATE_COLLECTION);
    }

    @Override
    public boolean changeTax(PropertyType propertyType, BigDecimal rate) {
        Query criteria = new Query(Criteria.where("propertyType").is(propertyType.toString()));
        Update update = new Update().set("tax", rate.toString());
        UpdateResult result = mongoTemplate.updateFirst(criteria, update, TaxRate.class, TAX_RATE_COLLECTION);

        return result.getModifiedCount() > 0;
    }

}
