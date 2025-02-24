package com.tvo.propertyregister;

import com.tvo.propertyregister.model.TaxRate;
import com.tvo.propertyregister.model.property.PropertyType;
import com.tvo.propertyregister.repository.TaxRateRepository;
import com.tvo.propertyregister.service.TaxRateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaxRateServiceTest {

    private static final TaxRate FLAT_RATE = new TaxRate(
            1,
            PropertyType.FLAT,
            new BigDecimal("6"));

    private static final TaxRate HOUSE_RATE = new TaxRate(
            2,
            PropertyType.HOUSE,
            new BigDecimal("8"));

    private static final TaxRate OFFICE_RATE = new TaxRate(
            3,
            PropertyType.OFFICE,
            new BigDecimal("13"));

    @Mock
    private TaxRateRepository taxRateRepository;

    @InjectMocks
    private TaxRateService taxRateService;


    @Test
    public void should_return_all_tax_rates() {
        List<TaxRate> expectedRates = List.of(FLAT_RATE, HOUSE_RATE, OFFICE_RATE);

        when(taxRateRepository.getAll()).thenReturn(expectedRates);

        List<TaxRate> result = taxRateService.getAll();

        assertEquals(expectedRates, result);
    }

    @Test
    public void should_change_tax_rate_for_flats() {
        BigDecimal newFlatRate = new BigDecimal("7");

        when(taxRateRepository.changeTax(PropertyType.FLAT, newFlatRate)).thenReturn(true);

        boolean result = taxRateService.changeTax(PropertyType.FLAT, newFlatRate);

        assertTrue(result);
    }

    @Test
    public void should_change_tax_rate_for_houses() {
        BigDecimal newHouseRate = new BigDecimal("12");

        when(taxRateRepository.changeTax(PropertyType.HOUSE, newHouseRate)).thenReturn(true);

        boolean result = taxRateService.changeTax(PropertyType.HOUSE, newHouseRate);

        assertTrue(result);
    }
}
