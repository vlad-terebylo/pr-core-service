package com.tvo.propertyregister.controller;

import com.tvo.propertyregister.model.owner.Owner;
import com.tvo.propertyregister.model.property.Property;
import com.tvo.propertyregister.service.OwnerService;
import com.tvo.propertyregister.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/owners")
public class OwnerController {

    private final OwnerService ownerService;
    private final PropertyService propertyService;

    @GetMapping
    public List<Owner> getAllOwners() {
        return this.ownerService.getAllOwners();
    }

    @GetMapping("/{id}")
    public Owner getOwnerById(@PathVariable int id) {
        return this.ownerService.getOwnerById(id);
    }

    @GetMapping("/debtors")
    public List<Owner> getDebtors() {
        return this.ownerService.findDebtors();
    }

    @GetMapping("/{ownerId}/properties")
    public List<Property> getAll(@PathVariable int ownerId) {
        return this.propertyService.getAll(ownerId);
    }

    @PostMapping
    public void addNewOwner(@RequestBody Owner owner) {
        this.ownerService.addNewOwner(owner);
    }


    @PostMapping("/{ownerId}/properties")
    public void addNewProperty(@PathVariable int ownerId, Property property) {
        this.propertyService.addNewProperty(ownerId, property);
    }

    @PutMapping("/{id}")
    public void updateInfo(@PathVariable int id, Owner owner) {
        this.ownerService.updateInfo(id, owner);
    }


    @PutMapping("/{ownerId}/properties/{propertyId}")
    public void updatePropertyInfo(@PathVariable int ownerId, @PathVariable int propertyId, Property property) {
        this.propertyService.updatePropertyInfo(ownerId, propertyId, property);
    }


    @DeleteMapping("/{id}")
    public void removeOwner(@PathVariable int id) {
        this.ownerService.removeOwner(id);
    }

    @DeleteMapping("/{ownerId}/properties/{propertyId}")
    public void remove(@PathVariable int ownerId, @PathVariable int propertyId) {
        this.propertyService.remove(ownerId, propertyId);
    }

}
