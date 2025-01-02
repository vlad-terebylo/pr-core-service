package com.tvo.propertyregister.repository.inmemory;

import com.tvo.propertyregister.model.owner.Owner;
import com.tvo.propertyregister.model.property.Property;
import com.tvo.propertyregister.repository.OwnerRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InMemoryOwnerRealization implements OwnerRepository {

    private static int ownerCounter = 1;
    private static int propertyCounter = 1;
    private final List<Owner> allOwners = new ArrayList<>();

    @Override
    public List<Owner> getAllOwners() {
        return this.allOwners;
    }

    @Override
    public Owner findById(int id) {
        for (Owner owner : this.allOwners) {
            if (owner.getId() == id) {
                return owner;
            }
        }

        return null;
    }

    @Override
    public List<Owner> findDebtors() {
        List<Owner> debtors = new ArrayList<>();

        for (Owner owner : this.allOwners) {
            if (owner.getTaxesDept().compareTo(BigDecimal.ZERO) > 0) {
                debtors.add(owner);
            }
        }

        return debtors;
    }

    @Override
    public boolean save(Owner owner) {
        owner.setId(ownerCounter++);

        for (Property property : owner.getProperties()) {
            property.setId(propertyCounter++);
        }

        return this.allOwners.add(owner);
    }

    @Override
    public boolean update(int id, Owner owner) {
        for (Property property : owner.getProperties()) {
            property.setId(propertyCounter++);
        }

        for (Owner currentOwner : this.allOwners) {
            if (currentOwner.getId() == id) {
                currentOwner.setFirstName(owner.getFirstName());
                currentOwner.setLastName(owner.getLastName());
                currentOwner.setFamilyStatus(owner.getFamilyStatus());
                currentOwner.setHasChildren(owner.isHasChildren());
                currentOwner.setEmail(owner.getEmail());
                currentOwner.setPhoneNumber(owner.getPhoneNumber());
                currentOwner.setTaxesDept(owner.getTaxesDept());
                currentOwner.setProperties(owner.getProperties());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean remove(int id) {
        return this.allOwners.removeIf(owner -> owner.getId() == id);
    }
}
