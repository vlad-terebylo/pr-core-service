package com.tvo.propertyregister.repository.mongodb;

import com.tvo.propertyregister.model.owner.Owner;
import com.tvo.propertyregister.repository.OwnerRepository;

import java.util.List;

public class MongoDbOwnerRepository implements OwnerRepository {
    @Override
    public List<Owner> getAllOwners() {
        return null;
    }

    @Override
    public Owner findById(int id) {
        return null;
    }

    @Override
    public List<Owner> findDebtors() {
        return null;
    }

    @Override
    public boolean save(Owner owner) {
        return true;
    }

    @Override
    public boolean update(int id, Owner owner) {
        return true;
    }

    @Override
    public boolean remove(int id) {
        return true;
    }

    @Override
    public void clear() {

    }
}
