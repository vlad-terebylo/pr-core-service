package com.tvo.propertyregister.repository.inmemory;

import com.tvo.propertyregister.model.owner.Owner;
import com.tvo.propertyregister.repository.OwnerRepository;

import java.util.List;

public class InMemoryOwnerRealization implements OwnerRepository {
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
    public void save(Owner owner) {

    }

    @Override
    public void update(int id, Owner owner) {

    }

    @Override
    public void remove(int id) {

    }
}
