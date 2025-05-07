package com.tvo.propertyregister.integration;

import com.tvo.propertyregister.integration.config.repository.TaxRateTestRepository;
import com.tvo.propertyregister.model.TaxRate;
import com.tvo.propertyregister.model.dto.BooleanResponseDto;
import com.tvo.propertyregister.model.dto.ChangeTaxRateRequest;
import com.tvo.propertyregister.model.property.PropertyType;
import com.tvo.propertyregister.service.TaxRateService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.*;

public class TaxRateServiceIntegrationTests extends AbstractServiceTest {

    @Autowired
    private TaxRateService taxRateService;

    @Autowired
    private TaxRateTestRepository taxRateTestRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.host", MONGO_DB_CONTAINER::getHost);
        registry.add("spring.data.mongodb.port", MONGO_DB_CONTAINER::getFirstMappedPort);
    }

    @BeforeAll
    public static void startContainer() {
        MONGO_DB_CONTAINER.start();
    }

    @AfterAll
    public static void stopContainer() {
        MONGO_DB_CONTAINER.stop();
    }

    @AfterEach
    void cleanUp() {
        taxRateTestRepository.clear();
    }

    @Test
    void should_get_tax_rates() {
        TaxRate newRate = new TaxRate(1, PropertyType.FLAT, new BigDecimal("11"));
        taxRateTestRepository.insertTaxRate(newRate);

        //        List<TaxRate> actualRates = taxRateService.getAll();

        ResponseEntity<List<TaxRate>> actualRatesResponse = testRestTemplate.exchange(
                "/v1/tax-rate",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        List<TaxRate> actualRates = requireNonNull(actualRatesResponse.getBody());

        assertEquals(HttpStatus.OK, actualRatesResponse.getStatusCode());
        assertEquals(1, actualRates.size());
    }

    @Test
    void should_change_tax_rate_for_flat() {
        taxRateTestRepository.initTaxRates();

        BigDecimal newTaxRate = new BigDecimal("10");
        HttpEntity<ChangeTaxRateRequest> request = new HttpEntity<>(new ChangeTaxRateRequest(newTaxRate));

//        taxRateService.changeTax(PropertyType.FLAT, newTaxRate);

        ResponseEntity<BooleanResponseDto> response = testRestTemplate.exchange(
                "/v1/tax-rate/" + PropertyType.FLAT,
                HttpMethod.PATCH,
                request,
                BooleanResponseDto.class
        );

        BooleanResponseDto booleanResponseDto = requireNonNull(response.getBody());

        List<TaxRate> actualRates = taxRateService.getAll();

        TaxRate actualFlatRate = actualRates.stream()
                .filter(currentRate -> PropertyType.FLAT.equals(currentRate.getPropertyType()))
                .findFirst()
                .orElseThrow();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(booleanResponseDto.succeed());
        assertEquals(newTaxRate, actualFlatRate.getTax());
    }

    @Test
    void should_change_tax_rate_for_house() {
        taxRateTestRepository.initTaxRates();

        BigDecimal newTaxRate = new BigDecimal("15");
//        taxRateService.changeTax(PropertyType.HOUSE, newTaxRate);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ChangeTaxRateRequest> request = new HttpEntity<>(new ChangeTaxRateRequest(newTaxRate), httpHeaders);

        ResponseEntity<BooleanResponseDto> response = testRestTemplate.exchange(
                "/v1/tax-rate/" + PropertyType.HOUSE,
                HttpMethod.PATCH,
                request,
                BooleanResponseDto.class
        );

        BooleanResponseDto booleanResponseDto = requireNonNull(response.getBody());

        List<TaxRate> actualRates = taxRateService.getAll();

        TaxRate actualHouseRate = actualRates.stream()
                .filter(currentRate -> PropertyType.HOUSE.equals(currentRate.getPropertyType()))
                .findFirst()
                .orElseThrow();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(booleanResponseDto.succeed());
        assertEquals(newTaxRate, actualHouseRate.getTax());
    }

    @Test
    void should_change_tax_rate_for_office() {
        taxRateTestRepository.initTaxRates();

        BigDecimal newTaxRate = new BigDecimal("20");
//        taxRateService.changeTax(PropertyType.OFFICE, newTaxRate);

        HttpEntity<ChangeTaxRateRequest> request = new HttpEntity<>(new ChangeTaxRateRequest(newTaxRate));

        ResponseEntity<BooleanResponseDto> response = testRestTemplate.exchange(
                "/v1/tax-rate/" + PropertyType.OFFICE,
                HttpMethod.PATCH,
                request,
                BooleanResponseDto.class
        );

        BooleanResponseDto booleanResponseDto = requireNonNull(response.getBody());

        List<TaxRate> actualRates = taxRateService.getAll();

        TaxRate actualOfficeRate = actualRates.stream()
                .filter(currentRate -> PropertyType.OFFICE.equals(currentRate.getPropertyType()))
                .findFirst()
                .orElseThrow();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(booleanResponseDto.succeed());
        assertEquals(newTaxRate, actualOfficeRate.getTax());
    }
}
