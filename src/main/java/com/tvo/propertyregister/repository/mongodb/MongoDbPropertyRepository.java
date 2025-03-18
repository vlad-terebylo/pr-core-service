package com.tvo.propertyregister.repository.mongodb;

import com.tvo.propertyregister.model.property.Property;
import com.tvo.propertyregister.repository.PropertyRepository;

import java.util.List;

public class MongoDbPropertyRepository implements PropertyRepository {
    @Override
    public List<Property> getAllProperties(int owner_id) {
        return null;
    }

    @Override
    public boolean save(int ownerId, Property property) {
        return true;
    }

    @Override
    public boolean update(int ownerId, int propertyId, Property property) {
        return true;
    }

    @Override
    public boolean remove(int ownerId, int propertyId) {
        return true;
    }


}
