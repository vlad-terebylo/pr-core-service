package com.tvo.propertyregister.integration.config.repository;

import com.mongodb.client.result.UpdateResult;
import com.tvo.propertyregister.model.owner.Owner;
import com.tvo.propertyregister.model.property.Property;
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
public class PropertyTestRepository {
    private static final String OWNERS_COLLECTION = "owners";

    private final MongoTemplate mongoTemplate;

    public List<Property> findAll(int ownerId) {
        Query criteria = new Query(Criteria.where("id").is(ownerId));
        List<Owner> owners = mongoTemplate.find(criteria, Owner.class, OWNERS_COLLECTION);

        return owners.get(0).getProperties();
    }

    public boolean save(Owner owner, Property property) {
        Query criteria = new Query(Criteria.where("id").is(owner.getId()));
        property.setId(getNextPropertyId());

        List<Property> allProperties = owner.getProperties();
        allProperties.add(property);

        Update update = new Update().set("properties", allProperties);
        UpdateResult result = mongoTemplate.updateFirst(criteria, update, Owner.class, OWNERS_COLLECTION);

        return result.getModifiedCount() > 0;
    }

    public boolean update(int ownerId, List<Property> properties) {
        Query criteria = new Query(Criteria.where("id").is(ownerId));

        Update update = new Update().set("properties", properties);
        UpdateResult result = mongoTemplate.updateFirst(criteria, update, Owner.class, OWNERS_COLLECTION);

        return result.getModifiedCount() > 0;
    }

    private int getNextPropertyId() {
        Query query = new Query(Criteria.where("_id").is("propertyId"));
        Update update = new Update().inc("sequence_value", 1);

        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true).upsert(true);

        Document counter = mongoTemplate.findAndModify(query, update, options, Document.class, "counters");

        return requireNonNull(counter).getInteger("sequence_value");
    }

    public void clear() {
        Query criteria = new Query();
        List<Owner> owners = mongoTemplate.find(criteria, Owner.class, OWNERS_COLLECTION);

        for (Owner owner : owners) {
            owner.setProperties(new ArrayList<>());

            Query updateQuery = new Query(Criteria.where("id").is(owner.getId()));
            Update update = new Update().set("properties", owner.getProperties());
            mongoTemplate.updateFirst(updateQuery, update, Owner.class, OWNERS_COLLECTION);
        }

        Query resetPropertyIdQuery = new Query(Criteria.where("_id").is("propertyId"));
        Update update = new Update().set("sequence_value", 1);
        mongoTemplate.updateFirst(resetPropertyIdQuery, update, Document.class, "counters");
    }
}
