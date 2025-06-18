package com.tvo.propertyregister.repository.mongodb;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.tvo.propertyregister.model.owner.Owner;
import com.tvo.propertyregister.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class MongoDbOwnerRepository implements OwnerRepository {

    private static final String OWNERS_COLLECTION = "owners";

    private final MongoTemplate mongoTemplate;

    @Override
    public List<Owner> findAll() {
        Query criteria = new Query();

        return mongoTemplate.find(criteria, Owner.class, OWNERS_COLLECTION);
    }

    @Override
    public Owner findById(int id) {
        Query criteria = new Query(Criteria.where("id").is(id));

        return mongoTemplate.findOne(criteria, Owner.class, OWNERS_COLLECTION);
    }

    @Override
    public List<Owner> findDebtors() {
        return findAllDebtors();
    }


    @Override
    public boolean save(Owner owner) {
        owner.setId(getNextOwnerId());
        mongoTemplate.save(owner, OWNERS_COLLECTION);
        return true;
    }

    @Override
    public boolean update(int id, Owner owner) {
        Query criteria = new Query(Criteria.where("id").is(id));
        Update update = new Update()
                .set("firstName", owner.getFirstName())
                .set("lastName", owner.getLastName())
                .set("age", owner.getAge())
                .set("familyStatus", owner.getFamilyStatus())
                .set("hasChildren", owner.isHasChildren())
                .set("email", owner.getEmail())
                .set("phoneNumber", owner.getPhoneNumber())
                .set("birthday", owner.getBirthday())
                .set("taxesDebt", owner.getTaxesDebt());
        UpdateResult result = mongoTemplate.updateFirst(criteria, update, Owner.class, OWNERS_COLLECTION);

        return result.getModifiedCount() > 0;
    }

    @Override
    public boolean remove(int id) {
        Query criteria = new Query(Criteria.where("id").is(id));
        DeleteResult result = mongoTemplate.remove(criteria, Owner.class, OWNERS_COLLECTION);

        return result.getDeletedCount() > 0;
    }

    @Override
    public BigDecimal countAllDebts() {
        var allDebtors = findAllDebtors();
        try {
            // emulating high load operation
            Thread.sleep(Duration.ofSeconds(1));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return allDebtors.stream()
                .map(Owner::getTaxesDebt)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<Owner> findAllDebtors() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("taxesDebt").gt("0")),
                Aggregation.project("id", "firstName", "lastName", "age", "familyStatus", "hasChildren", "email", "phoneNumber", "birthday", "properties")
                        .andExpression("toDouble(taxesDebt)").as("taxesDebt")
        );

        AggregationResults<Owner> results = mongoTemplate.aggregate(aggregation, OWNERS_COLLECTION, Owner.class);
        return results.getMappedResults();
    }

    private int getNextOwnerId() {
        Query query = new Query(Criteria.where("_id").is("ownerId"));
        Update update = new Update().inc("sequence_value", 1);

        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true).upsert(true);

        Document counter = mongoTemplate.findAndModify(query, update, options, Document.class, "counters");

        return Objects.requireNonNull(counter).getInteger("sequence_value");
    }

}
