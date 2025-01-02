package com.tvo.propertyregister.config;

import com.tvo.propertyregister.repository.ComplainRepository;
import com.tvo.propertyregister.repository.OwnerRepository;
import com.tvo.propertyregister.repository.PropertyRepository;
import com.tvo.propertyregister.repository.TaxRateRepository;
import com.tvo.propertyregister.repository.inmemory.InMemoryComplainRepository;
import com.tvo.propertyregister.repository.inmemory.InMemoryOwnerRealization;
import com.tvo.propertyregister.repository.inmemory.InMemoryPropertyRealization;
import com.tvo.propertyregister.repository.inmemory.InMemoryTaxRateRealization;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public OwnerRepository ownerRealization() {
        return new InMemoryOwnerRealization();
    }

    @Bean
    public TaxRateRepository taxRateRepository() {
        return new InMemoryTaxRateRealization();
    }

    @Bean
    public PropertyRepository propertyRealization() {
        return new InMemoryPropertyRealization();
    }

    @Bean
    public ComplainRepository complainRealization() {
        return new InMemoryComplainRepository();
    }
}
