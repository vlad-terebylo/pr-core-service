package com.tvo.propertyregister.controller;

import com.tvo.propertyregister.model.owner.Owner;
import com.tvo.propertyregister.service.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/owners")
public class OwnerController {

    private final OwnerService ownerService;

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

    @PostMapping
    public void addNewOwner(@RequestBody Owner owner) {
        this.ownerService.addNewOwner(owner);
    }

    @PutMapping("/{id}")
    public void updateInfo(@PathVariable int id, Owner owner) {
        this.ownerService.updateInfo(id, owner);
    }

    @DeleteMapping("/{id}")
    public void removeOwner(@PathVariable int id) {
        this.ownerService.removeOwner(id);
    }

}
