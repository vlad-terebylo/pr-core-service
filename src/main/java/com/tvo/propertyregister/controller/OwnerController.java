package com.tvo.propertyregister.controller;

import com.tvo.propertyregister.model.dto.*;
import com.tvo.propertyregister.model.owner.Owner;
import com.tvo.propertyregister.model.property.Property;
import com.tvo.propertyregister.service.OwnerService;
import com.tvo.propertyregister.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/owners")
public class OwnerController {

    private final OwnerService ownerService;
    private final PropertyService propertyService;

    @GetMapping
    public ResponseEntity<List<Owner>> getAllOwners() {
        return ResponseEntity.ok(this.ownerService.getAllOwners());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Owner> getOwnerById(@PathVariable int id) {
        return ResponseEntity.ok(this.ownerService.getOwnerById(id));
    }

    @GetMapping("/debtors")
    public ResponseEntity<List<Owner>> getDebtors() {
        return ResponseEntity.ok(this.ownerService.findDebtors());
    }

    @GetMapping("/{ownerId}/properties")
    public ResponseEntity<List<Property>> getAllPropertiesByOwnerId(@PathVariable int ownerId) {
        return ResponseEntity.ok(this.propertyService.getAll(ownerId));
    }

    @PostMapping
    public ResponseEntity<BooleanResponseDto> addNewOwner(@RequestBody CreateOwnerDto createOwnerDto) {
        return ResponseEntity.ok(new BooleanResponseDto(this.ownerService.addNewOwner(new Owner(createOwnerDto))));
    }

    @PostMapping("/{ownerId}/properties")
    public ResponseEntity<BooleanResponseDto> addNewProperty(
            @PathVariable int ownerId,
            @RequestBody CreatePropertyDto createPropertyDto) {
        return ResponseEntity.ok(new BooleanResponseDto(this.propertyService.add(ownerId, new Property(createPropertyDto))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BooleanResponseDto> updateOwnerInfo(
            @PathVariable int id,
            @RequestBody UpdateOwnerDto updateOwnerDto) {
        return ResponseEntity.ok(new BooleanResponseDto(this.ownerService.updateInfo(id, new Owner(updateOwnerDto))));
    }

    @PutMapping("/{ownerId}/properties/{propertyId}")
    public ResponseEntity<BooleanResponseDto> updatePropertyInfo(
            @PathVariable int ownerId,
            @PathVariable int propertyId,
            @RequestBody UpdatePropertyDto updatePropertyDto) {
        return ResponseEntity.ok(new BooleanResponseDto(this.propertyService.update(ownerId, propertyId, new Property(updatePropertyDto))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BooleanResponseDto> removeOwner(@PathVariable int id) {
        return ResponseEntity.ok(new BooleanResponseDto(this.ownerService.removeOwner(id)));
    }

    @DeleteMapping("/{ownerId}/properties/{propertyId}")
    public ResponseEntity<BooleanResponseDto> removeProperty(
            @PathVariable int ownerId,
            @PathVariable int propertyId) {
        return ResponseEntity.ok(new BooleanResponseDto(this.propertyService.remove(ownerId, propertyId)));
    }

    @GetMapping("/{ownerId}/tax-obligations")
    public ResponseEntity<TaxObligationResponseDto> countTaxObligation(@PathVariable int ownerId) {
        return ResponseEntity.ok(new TaxObligationResponseDto(this.ownerService.countTaxObligation(ownerId)));
    }

    @GetMapping("/totalDebt")
    public ResponseEntity<BigDecimal> countTotalNumberOfDebts() {
        return ResponseEntity.ok(new BigDecimal(ownerService.countTotalDebt()));
    }

}
