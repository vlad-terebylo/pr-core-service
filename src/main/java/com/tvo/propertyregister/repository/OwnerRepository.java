package com.tvo.propertyregister.repository;

import com.tvo.propertyregister.model.owner.Owner;

import java.util.List;

public interface OwnerRepository {

    List<Owner> getAllOwners();

    Owner findById(int id);

    List<Owner> findDebtors();

    boolean save(Owner owner);

    boolean update(int id, Owner owner);

    boolean remove(int id);

    void clear();
}
