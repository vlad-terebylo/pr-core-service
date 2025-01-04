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

    public boolean addNewOwner(Owner owner) {
        return this.ownerRepository.save(owner);
    }

    public boolean updateInfo(int id, Owner owner) {
        return this.ownerRepository.update(id, owner);
    }

    public boolean removeOwner(int id) {
        return this.ownerRepository.remove(id);
    }

    public BigDecimal countTaxObligation(int id) {
        Owner owner = this.ownerRepository.findById(id);

        BigDecimal leeway = new BigDecimal("1");
        BigDecimal taxObligation = countBaseTax(owner);

        if (owner.isHasChildren()) {
            if (FamilyStatus.SINGLE.equals(owner.getFamilyStatus())) {
                leeway = leeway.subtract(new BigDecimal("0.3"));
            } else {
                leeway = leeway.subtract(new BigDecimal("0.1"));
            }
        }
        if (FamilyStatus.MARRIED.equals(owner.getFamilyStatus())) {
            leeway = leeway.subtract(new BigDecimal("0.1"));
        }

        return taxObligation.multiply(leeway);
    }

    private BigDecimal countBaseTax(Owner owner) {
        List<Property> properties = owner.getProperties();
        List<TaxRate> taxRates = this.taxRateService.getAll();

        BigDecimal FLAT_TAX = taxRates.get(0).getTax();
        BigDecimal HOUSE_TAX = taxRates.get(1).getTax();
        BigDecimal OFFICE_TAX = taxRates.get(2).getTax();

        BigDecimal baseTax = new BigDecimal("0");

        for (Property property : properties) {
            BigDecimal square = new BigDecimal(property.getSquare());
            if (property.getPropertyType() == PropertyType.FLAT) {
                baseTax = baseTax.add(square.multiply(FLAT_TAX));
            } else if (property.getPropertyType() == PropertyType.HOUSE) {
                baseTax = baseTax.add(square.multiply(HOUSE_TAX));
            } else {
                baseTax = baseTax.add(square.multiply(OFFICE_TAX));
            }
        }

        return baseTax;
    }
}
