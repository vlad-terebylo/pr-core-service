package com.tvo.propertyregister.service;

import com.tvo.propertyregister.exception.PropertyNotFoundException;
import com.tvo.propertyregister.model.property.Property;
import com.tvo.propertyregister.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public List<Property> getAll(int ownerId) {
        return this.propertyRepository.findAll(ownerId);
    }

    public boolean addNewProperty(int ownerId, Property property) {
        if (Objects.isNull(property)) {
            throw new PropertyNotFoundException("This property is empty");
        }

        return this.propertyRepository.save(ownerId, property);
    }

    public boolean updatePropertyInfo(int ownerId, int propertyId, Property property) {
        if (Objects.isNull(property)) {
            throw new PropertyNotFoundException("This property is empty");
        }

        return this.propertyRepository.update(ownerId, propertyId, property);
    }

    public boolean remove(int ownerId, int propertyId) {
        return this.propertyRepository.remove(ownerId, propertyId);
    }
}
