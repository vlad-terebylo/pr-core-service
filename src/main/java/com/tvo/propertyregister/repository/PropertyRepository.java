package com.tvo.propertyregister.repository;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.tvo.propertyregister.model.property.Property;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface PropertyRepository {

    List<Property> getAllProperties(int owner_id);

    boolean save(int ownerId, Property property);

    boolean update(int ownerId, int propertyId, Property property);

    boolean remove(int ownerId, int propertyId);

    void clear();
}
