package com.tvo.propertyregister.service;

import com.tvo.propertyregister.exception.NoSuchOwnerException;
import com.tvo.propertyregister.exception.PropertyNotFoundException;
import com.tvo.propertyregister.model.owner.Owner;
import com.tvo.propertyregister.model.property.Property;
import com.tvo.propertyregister.repository.OwnerRepository;
import com.tvo.propertyregister.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final OwnerRepository ownerRepository;

    public List<Property> getAll(int ownerId) {
        Owner owner = ownerRepository.findById(ownerId);
        if (Objects.isNull(owner)) {
            throw new NoSuchOwnerException("The owner with id " + ownerId + " was not found");
        }

        return this.propertyRepository.findAll(ownerId);
    }

    public boolean addNewProperty(int ownerId, Property property) {
        if (Objects.isNull(property)) {
            throw new PropertyNotFoundException("This property is empty");
        }

        Owner owner = ownerRepository.findById(ownerId);
        if (Objects.isNull(owner)) {
            throw new NoSuchOwnerException("Owner with id " + ownerId + " not found");
        }

        if (Objects.isNull(owner.getProperties())) {
            owner.setProperties(new ArrayList<>());
        }

        List<Property> properties = owner.getProperties();
        properties.add(property);
        owner.setProperties(properties);

        return this.propertyRepository.save(ownerId, owner.getProperties());
    }

    public boolean updatePropertyInfo(int ownerId, int propertyId, Property property) {
        if (Objects.isNull(property)) {
            throw new PropertyNotFoundException("This property is empty");
        }

        Owner owner = ownerRepository.findById(ownerId);
        if (Objects.isNull(owner)) {
            throw new NoSuchOwnerException("Owner with id " + ownerId + " not found");
        }

        Property propertyToUpdate = owner.getProperties().stream()
                .filter(currentProperty -> currentProperty.getId() == propertyId)
                .findFirst()
                .orElseThrow(() -> new PropertyNotFoundException("Property not found with ID: " + propertyId));


        propertyToUpdate.setNumberOfRooms(property.getNumberOfRooms());
        propertyToUpdate.setCost(property.getCost());
        propertyToUpdate.setDateOfBecomingOwner(property.getDateOfBecomingOwner());

        return this.propertyRepository.update(owner);
    }

    public boolean remove(int ownerId, int propertyId) {
        Owner owner = ownerRepository.findById(ownerId);
        if (Objects.isNull(owner)) {
            throw new NoSuchOwnerException("Owner with id " + ownerId + " not found");
        }

        List<Property> filtered = owner.getProperties().stream()
                .filter(p -> p.getId() != propertyId)
                .toList();

        if (filtered.size() == owner.getProperties().size()) {
            throw new PropertyNotFoundException("Property with id " + propertyId + " not found");
        }

        return this.propertyRepository.remove(ownerId, filtered);
    }
}
