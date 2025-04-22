package com.tvo.propertyregister.repository;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.tvo.propertyregister.model.owner.Owner;
import com.tvo.propertyregister.model.property.Property;

import java.util.List;

public interface PropertyRepository {

    List<Property> findAll(int ownerId);

    boolean save(Owner owner, Property property);

    boolean update(int ownerId, List<Property> updatedProperties);

}
