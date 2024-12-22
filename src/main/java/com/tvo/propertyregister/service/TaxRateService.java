package com.tvo.propertyregister.service;

import com.tvo.propertyregister.model.TaxRate;
import com.tvo.propertyregister.model.property.PropertyType;
import com.tvo.propertyregister.repository.TaxRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaxRateService {

    private final TaxRateRepository taxRateRepository;

    public List<TaxRate> getAll(){
        return this.taxRateRepository.getAll();
    }

    public void changeTax(PropertyType propertyType, TaxRate taxRate) {
        this.taxRateRepository.changeTax(propertyType, taxRate);
    }
}
