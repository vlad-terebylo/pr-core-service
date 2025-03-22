package com.tvo.propertyregister.integration;

import com.tvo.propertyregister.integration.config.TestConfig;
import com.tvo.propertyregister.model.TaxRate;
import com.tvo.propertyregister.model.property.PropertyType;
import com.tvo.propertyregister.repository.TaxRateRepository;
import com.tvo.propertyregister.service.TaxRateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
public class TaxRateServiceIntegrationTests {

    @Autowired
    private TaxRateService taxRateService;

    @Autowired
    private TaxRateRepository taxRateRepository;

    private static final List<TaxRate> TAX_RATES = List.of(
            new TaxRate(1, PropertyType.FLAT, new BigDecimal("6")),
            new TaxRate(2, PropertyType.HOUSE, new BigDecimal("8")),
            new TaxRate(3, PropertyType.OFFICE, new BigDecimal("13"))
    );

    @BeforeEach
    void initTaxRates() {
        cleanUp();
    }

    private void cleanUp() {
        taxRateRepository.changeTax(PropertyType.FLAT, new BigDecimal(6));
        taxRateRepository.changeTax(PropertyType.HOUSE, new BigDecimal(8));
        taxRateRepository.changeTax(PropertyType.OFFICE, new BigDecimal(13));
    }

    @Test
    void should_get_tax_rates() {
        List<TaxRate> actualRates = taxRateService.getAll();

        assertEquals(TAX_RATES, actualRates);
    }

    @Test
    void should_change_tax_rate_for_flat() {
        BigDecimal newTaxRate = new BigDecimal("10");

        taxRateService.changeTax(PropertyType.FLAT, newTaxRate);

        List<TaxRate> actualRates = taxRateService.getAll();

        TaxRate actualFlatRate = actualRates.stream()
                .filter(currentRate -> PropertyType.FLAT.equals(currentRate.getPropertyType()))
                .findFirst()
                .orElseThrow();

        assertEquals(newTaxRate, actualFlatRate.getTax());
    }

    @Test
    void should_change_tax_rate_for_house() {
        BigDecimal newTaxRate = new BigDecimal("15");

        taxRateService.changeTax(PropertyType.HOUSE, newTaxRate);

        List<TaxRate> actualRates = taxRateService.getAll();

        TaxRate actualHouseRate = actualRates.stream()
                .filter(currentRate -> PropertyType.HOUSE.equals(currentRate.getPropertyType()))
                .findFirst()
                .orElseThrow();

        assertEquals(newTaxRate, actualHouseRate.getTax());
    }

    @Test
    void should_change_tax_rate_for_office() {
        BigDecimal newTaxRate = new BigDecimal("20");

        taxRateService.changeTax(PropertyType.OFFICE, newTaxRate);

        List<TaxRate> actualRates = taxRateService.getAll();

        TaxRate actualOfficeRate = actualRates.stream()
                .filter(currentRate -> PropertyType.OFFICE.equals(currentRate.getPropertyType()))
                .findFirst()
                .orElseThrow();

        assertEquals(newTaxRate, actualOfficeRate.getTax());
    }
}
