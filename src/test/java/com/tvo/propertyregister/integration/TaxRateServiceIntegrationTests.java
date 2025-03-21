package com.tvo.propertyregister.integration;

import com.tvo.propertyregister.integration.config.TestConfig;
import com.tvo.propertyregister.repository.TaxRateRepository;
import com.tvo.propertyregister.service.TaxRateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
public class TaxRateServiceIntegrationTests {

    @Autowired
    private TaxRateService taxRateService;

    @Autowired
    private TaxRateRepository taxRateRepository;

    @BeforeEach
    void initTaxRates() {
        taxRateRepository.init();
    }

    @Test
    void should_get_tax_rates_if_tax_rate_list_is_empty(){

    }

    @Test
    void should_get_tax_rates(){

    }

    @Test
    void should_change_tax_rate_for_flat(){

    }

    @Test
    void should_change_tax_rate_for_house(){

    }

    @Test
    void should_change_tax_rate_for_office(){

    }

    @Test
    void should_change_tax_rate_if_such_type_does_not_exists(){

    }
}
