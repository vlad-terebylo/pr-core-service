package com.tvo.propertyregister.controller;

import com.tvo.propertyregister.model.TaxRate;
import com.tvo.propertyregister.model.property.PropertyType;
import com.tvo.propertyregister.service.TaxRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/tax-rate")
public class TaxRateController {

    private final TaxRateService taxRateService;

    @GetMapping
    public List<TaxRate> getAll() {
        return this.taxRateService.getAll();
    }

    @PatchMapping("/{propertyType}")
    public void changeTax(@PathVariable PropertyType propertyType, TaxRate taxRate) {
        this.taxRateService.changeTax(propertyType, taxRate);
    }

}
