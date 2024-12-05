package com.tvo.propertyregister.repository.mongoDB;

import com.tvo.propertyregister.model.property.Property;
import com.tvo.propertyregister.repository.PropertyRepository;

import java.util.List;

public class MongoDBPropertyRealization implements PropertyRepository {
    @Override
    public List<Property> getAllProperties(int owner_id) {
        return null;
    }

    @Override
    public void save(int ownerId, Property property) {

    }

    @Override
    public void update(int ownerId, int propertyId, Property property) {

    }

    @Override
    public void remove(int ownerId, int propertyId) {

    }


}
