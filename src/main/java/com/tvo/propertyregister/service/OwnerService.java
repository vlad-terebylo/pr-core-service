package com.tvo.propertyregister.service;

import com.tvo.propertyregister.model.TaxRate;
import com.tvo.propertyregister.model.owner.FamilyStatus;
import com.tvo.propertyregister.model.owner.Owner;
import com.tvo.propertyregister.model.property.Property;
import com.tvo.propertyregister.model.property.PropertyType;
import com.tvo.propertyregister.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerRepository ownerRepository;
    private final TaxRateService taxRateService;

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

    public BigDecimal countTaxObligation(int id) {
        Owner owner = this.ownerRepository.findById(id);

        BigDecimal leeway = new BigDecimal("1");
        BigDecimal taxObligation = countBaseTax(owner);

        if (owner.isHasChildren()) {
            leeway = leeway.subtract(new BigDecimal("0.1"));
        }
        if (owner.getFamilyStatus() == FamilyStatus.MARRIED) {
            leeway = leeway.subtract(new BigDecimal("0.1"));
        }

        return taxObligation.multiply(leeway);
    }

    private BigDecimal countBaseTax(Owner owner) {
        List<Property> properties = owner.getProperties();
        List<TaxRate> taxRates = this.taxRateService.getAll();

        BigDecimal FLAT_TAX = taxRates.get(1).getTax();
        BigDecimal HOUSE_TAX = taxRates.get(2).getTax();
        BigDecimal OFFICE_TAX = taxRates.get(3).getTax();

        BigDecimal baseTax = new BigDecimal("0");

        for (Property property : properties) {
            BigDecimal square = new BigDecimal(property.getSquare());
            if (property.getPropertyType() == PropertyType.FLAT) {
                baseTax = baseTax.multiply(square.multiply(FLAT_TAX));
            } else if (property.getPropertyType() == PropertyType.HOUSE) {
                baseTax = baseTax.multiply(square.multiply(HOUSE_TAX));
            } else {
                baseTax = baseTax.multiply(square.multiply(OFFICE_TAX));
            }
        }

        return baseTax;
    }
}
