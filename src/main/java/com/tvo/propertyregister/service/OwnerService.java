package com.tvo.propertyregister.service;

import com.tvo.propertyregister.model.owner.Owner;
import com.tvo.propertyregister.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerRepository ownerRepository;

    public List<Owner> getAllOwners() {
        return this.ownerRepository.getAllOwners();
    }

    public Owner getOwnerById(int id) {
        return this.ownerRepository.findById(id);
    }

    public List<Owner> findDebtors() {
        return this.ownerRepository.findDebtors();
    }

    public void addNewOwner(Owner owner) {
        this.ownerRepository.save(owner);
    }

    public void updateInfo(int id, Owner owner) {
        this.ownerRepository.update(id, owner);
    }

    public void removeOwner(int id) {
        this.ownerRepository.remove(id);
    }
}
