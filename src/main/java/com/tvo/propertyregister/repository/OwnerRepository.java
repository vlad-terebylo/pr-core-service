package com.tvo.propertyregister.repository;

import com.tvo.propertyregister.model.owner.Owner;

import java.util.List;

public interface OwnerRepository {

    List<Owner> getAllOwners();

    Owner findById(int id);

    List<Owner> findDebtors();

    void save(Owner owner);

    void update(int id, Owner owner);

    void remove(int id);
}
