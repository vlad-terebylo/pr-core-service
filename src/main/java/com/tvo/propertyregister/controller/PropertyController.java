package com.tvo.propertyregister.controller;

import com.tvo.propertyregister.model.property.Property;
import com.tvo.propertyregister.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/owners/{owner_id}/properties")
public class PropertyController {

    private final PropertyService propertyService;

    @GetMapping
    public List<Property> getAll(@PathVariable int owner_id) {
        return this.propertyService.getAll(owner_id);
    }

    @PostMapping
    public void addNewProperty(@PathVariable int owner_id, Property property) {
        this.propertyService.addNewProperty(owner_id, property);
    }

    @PutMapping("/{property_id}")
    public void updatePropertyInfo(@PathVariable int owner_id, @PathVariable int property_id, Property property){
        this.propertyService.updatePropertyInfo(owner_id, property_id, property);
    }

    @DeleteMapping("/{property_id}")
    public void remove(@PathVariable int owner_id, @PathVariable int property_id){
        this.propertyService.remove(owner_id, property_id);
    }

}
