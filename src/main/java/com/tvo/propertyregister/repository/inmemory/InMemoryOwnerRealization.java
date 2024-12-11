package com.tvo.propertyregister.repository.inmemory;

import com.tvo.propertyregister.model.owner.Owner;
import com.tvo.propertyregister.repository.OwnerRepository;

import java.util.ArrayList;
import java.util.List;

public class InMemoryOwnerRealization implements OwnerRepository {

    private List<Owner> allOwners = new ArrayList<>();

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
            if (owner.getTaxesDept() > 0) {
                debtors.add(owner);
            }
        }

        return debtors;
    }

    @Override
    public void save(Owner owner) {
        this.allOwners.add(owner);
    }

    @Override
    public void update(int id, Owner owner) {

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
            }
        }
    }

    @Override
    public void remove(int id) {
        this.allOwners.removeIf(owner -> owner.getId() == id);
    }
}
