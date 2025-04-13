package com.tvo.propertyregister.repository;

import com.tvo.propertyregister.model.owner.Owner;
import com.tvo.propertyregister.model.property.Property;

import java.util.List;

public interface PropertyRepository {

    List<Property> findAll(int owner_id);

    boolean save(int ownerId, List<Property> properties);

    boolean update(Owner owner);

    boolean remove(int ownerId, List<Property> updatedProperties);
}
