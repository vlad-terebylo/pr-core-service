package com.tvo.propertyregister.repository.mongodb;

import com.mongodb.client.result.UpdateResult;
import com.tvo.propertyregister.model.owner.Owner;
import com.tvo.propertyregister.model.property.Property;
import com.tvo.propertyregister.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public class MongoDbPropertyRepository implements PropertyRepository {

    private static final String OWNERS_COLLECTION = "owners";

    private final MongoTemplate mongoTemplate;

    @Override
    public List<Property> findAll(int ownerId) {
        Query criteria = new Query(Criteria.where("id").is(ownerId));
        List<Owner> owners = mongoTemplate.find(criteria, Owner.class, OWNERS_COLLECTION);

        return owners.get(0).getProperties();
    }

    @Override
    public boolean save(int ownerId, Property property) {
        Query criteria = new Query(Criteria.where("id").is(ownerId));
        property.setId(getNextPropertyId());

        Owner owner = mongoTemplate.findOne(criteria, Owner.class, OWNERS_COLLECTION);
        List<Property> properties = requireNonNull(owner).getProperties();
        properties.add(property);
        owner.setProperties(properties);

        mongoTemplate.save(owner, OWNERS_COLLECTION);
        return true;
    }

    @Override
    public boolean update(int ownerId, int propertyId, Property property) {
        // find owner
        Query criteria = new Query(Criteria.where("id").is(ownerId));
        Owner owner = requireNonNull(mongoTemplate.findOne(criteria, Owner.class, OWNERS_COLLECTION));

        // get property in owner.getProperties() without property with propertyId
        List<Property> filteredProperties = new ArrayList<>(owner.getProperties().stream()
                .filter(currentProperty -> currentProperty.getId() != propertyId)
                .toList());

        // to add updated property
        filteredProperties.add(property);

        // to update
        Update update = new Update()
                .set("properties", filteredProperties);
        UpdateResult result = mongoTemplate.updateFirst(criteria, update, Owner.class, OWNERS_COLLECTION);

        return result.getModifiedCount() > 0;
    }

    @Override
    public boolean remove(int ownerId, int propertyId) {
        Query criteria = new Query(Criteria.where("id").is(ownerId));
        Owner owner = mongoTemplate.findOne(criteria, Owner.class, OWNERS_COLLECTION);

        List<Property> filteredProperties = requireNonNull(owner).getProperties().stream()
                .filter(property -> property.getId() != propertyId)
                .toList();

        owner.setProperties(filteredProperties);

        Update update = new Update()
                .set("properties", filteredProperties);
        UpdateResult result = mongoTemplate.updateFirst(criteria, update, Owner.class, OWNERS_COLLECTION);

        return result.getModifiedCount() > 0;
    }

    @Override
    public void clear() {

    }

    private int getNextPropertyId() {
        Query query = new Query(Criteria.where("_id").is("propertyId"));
        Update update = new Update().inc("sequence_value", 1);

        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true).upsert(true);

        Document counter = mongoTemplate.findAndModify(query, update, options, Document.class, "counters");

        return requireNonNull(counter).getInteger("sequence_value");
    }


}
