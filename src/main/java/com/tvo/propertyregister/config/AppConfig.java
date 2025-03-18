package com.tvo.propertyregister.config;

import com.tvo.propertyregister.repository.OwnerRepository;
import com.tvo.propertyregister.repository.PropertyRepository;
import com.tvo.propertyregister.repository.TaxRateRepository;
import com.tvo.propertyregister.repository.inmemory.InMemoryOwnerRepository;
import com.tvo.propertyregister.repository.inmemory.InMemoryPropertyRepository;
import com.tvo.propertyregister.repository.inmemory.InMemoryTaxRateRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public OwnerRepository ownerRealization() {
        return new InMemoryOwnerRepository();
    }

    @Bean
    public TaxRateRepository taxRateRepository() {
        return new InMemoryTaxRateRepository();
    }

    @Bean
    public PropertyRepository propertyRealization() {
        return new InMemoryPropertyRepository();
    }
}
