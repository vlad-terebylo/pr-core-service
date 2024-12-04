package com.tvo.propertyregister.repository.mongoDB;

import com.tvo.propertyregister.model.property.Property;
import com.tvo.propertyregister.repository.PropertyRepository;

import java.util.List;

public class MongoDBPropertyRealization implements PropertyRepository {
    @Override
    public List<Property> getAllProperties() {
        return null;
    }

    @Override
    public Property getById(int id) {
        return null;
    }

    @Override
    public void save(Property property) {

    }

    @Override
    public void update(int id, Property property) {

    }

    @Override
    public void remove(int id) {

    }
}
