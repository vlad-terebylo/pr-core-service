package com.tvo.propertyregister.controller;

import com.tvo.propertyregister.model.BooleanResponseDto;
import com.tvo.propertyregister.model.ChangeTaxRateRequest;
import com.tvo.propertyregister.model.TaxRate;
import com.tvo.propertyregister.model.property.PropertyType;
import com.tvo.propertyregister.service.TaxRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/tax-rate")
public class TaxRateController {

    private final TaxRateService taxRateService;

    @GetMapping
    public ResponseEntity<List<TaxRate>> getAll() {
        return ResponseEntity.ok(this.taxRateService.getAll());
    }

    @PatchMapping("/{propertyType}")
    public ResponseEntity<BooleanResponseDto> changeTax(@PathVariable String propertyType, @RequestBody ChangeTaxRateRequest request) {
        return ResponseEntity.ok(new BooleanResponseDto(this.taxRateService.changeTax(PropertyType.valueOf(propertyType.toUpperCase()), request.rate())));
    }

}
