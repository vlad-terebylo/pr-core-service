package com.tvo.propertyregister.repository;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.tvo.propertyregister.model.property.Property;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface PropertyRepository {

    List<Property> getAllProperties();

    Property getById(int id);

    void save(Property property);

    void update(int id, Property property);

    void remove(int id);
}
