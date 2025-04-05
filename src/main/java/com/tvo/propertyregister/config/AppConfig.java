package com.tvo.propertyregister.config;

import com.tvo.propertyregister.repository.OwnerRepository;
import com.tvo.propertyregister.repository.PropertyRepository;
import com.tvo.propertyregister.repository.TaxRateRepository;
import com.tvo.propertyregister.repository.inmemory.InMemoryOwnerRepository;
import com.tvo.propertyregister.repository.inmemory.InMemoryPropertyRepository;
import com.tvo.propertyregister.repository.inmemory.InMemoryTaxRateRepository;
import com.tvo.propertyregister.repository.mongodb.MongoDbOwnerRepository;
import com.tvo.propertyregister.repository.mongodb.MongoDbPropertyRepository;
import com.tvo.propertyregister.repository.mongodb.MongoDbTaxRateRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class AppConfig {

    @Bean
    public OwnerRepository ownerRealization(MongoTemplate mongoTemplate) {
        return new MongoDbOwnerRepository(mongoTemplate);
    }

    @Bean
    public TaxRateRepository taxRateRepository(MongoTemplate mongoTemplate) {
        return new MongoDbTaxRateRepository(mongoTemplate);
    }

    @Bean
    public PropertyRepository propertyRealization(MongoTemplate mongoTemplate) {
        return new MongoDbPropertyRepository(mongoTemplate);
    }
}
