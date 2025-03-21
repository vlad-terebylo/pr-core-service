package com.tvo.propertyregister.service;

import com.tvo.propertyregister.model.TaxRate;
import com.tvo.propertyregister.model.property.PropertyType;
import com.tvo.propertyregister.repository.TaxRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaxRateService {

    private final TaxRateRepository taxRateRepository;

    public List<TaxRate> getAll() {
        return this.taxRateRepository.getAll();
    }

    public boolean changeTax(PropertyType propertyType, BigDecimal rate) {
        return this.taxRateRepository.changeTax(propertyType, rate);
    }


}
