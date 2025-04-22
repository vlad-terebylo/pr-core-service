package com.tvo.propertyregister.integration.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.tvo.propertyregister.integration.config.repository.OwnerTestRepository;
import com.tvo.propertyregister.integration.config.repository.PropertyTestRepository;
import com.tvo.propertyregister.integration.config.repository.TaxRateTestRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;

@TestConfiguration
public class TestMongoConfig {
    @Value("${spring.data.mongodb.host}")
    private String mongoHost;

    @Value("${spring.data.mongodb.port}")
    private String mongoPort;

    @Bean
    @Primary
    public MongoClient testMongoClient() {
        return MongoClients.create("mongodb://" + mongoHost + ":" + mongoPort);
    }

    @Bean
    public OwnerTestRepository ownerTestRepository(MongoTemplate mongoTemplate){
        return new OwnerTestRepository(mongoTemplate);
    }

    @Bean
    public PropertyTestRepository propertyTestRepository(MongoTemplate mongoTemplate){
        return new PropertyTestRepository(mongoTemplate);
    }

    @Bean
    public TaxRateTestRepository taxRatesTestRepository(MongoTemplate mongoTemplate){
        return new TaxRateTestRepository(mongoTemplate);
    }
}
