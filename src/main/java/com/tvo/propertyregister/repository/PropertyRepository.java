package com.tvo.propertyregister.repository;

import com.tvo.propertyregister.model.property.Property;

import java.util.List;

public interface PropertyRepository {

    List<Property> findAll(int owner_id);

    boolean save(int ownerId, Property property);

    boolean update(int ownerId, int propertyId, Property property);

    boolean remove(int ownerId, int propertyId);

    void clear();
}
