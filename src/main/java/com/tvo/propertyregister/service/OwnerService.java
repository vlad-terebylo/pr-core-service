package com.tvo.propertyregister.service;

import com.tvo.propertyregister.exception.InvalidTaxRateNumberException;
import com.tvo.propertyregister.exception.NoSuchOwnerException;
import com.tvo.propertyregister.exception.PropertyNotFoundException;
import com.tvo.propertyregister.exception.UpdateOwnerFailedException;
import com.tvo.propertyregister.model.TaxRate;
import com.tvo.propertyregister.model.owner.FamilyStatus;
import com.tvo.propertyregister.model.owner.Owner;
import com.tvo.propertyregister.model.property.Property;
import com.tvo.propertyregister.model.property.PropertyType;
import com.tvo.propertyregister.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

import static com.tvo.propertyregister.service.utils.Constants.TAXES_RATE_NUMBER;

@Service
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerRepository ownerRepository;
    private final TaxRateService taxRateService;

    public List<Owner> getAllOwners() {
        return this.ownerRepository.findAll();
    }

    public Owner getOwnerById(int id) {
        Owner owner = ownerRepository.findById(id);
        if (Objects.isNull(owner)) {
            throw new NoSuchOwnerException("The owner with id " + id + " was not found");
        }

        return this.ownerRepository.findById(id);
    }

    public List<Owner> findDebtors() {
        return this.ownerRepository.findDebtors();
    }

    public void recountDebtForDebtors() {
        List<Owner> allDebtors = this.ownerRepository.findDebtors();

        allDebtors.stream()
                .filter(debtor -> debtor.getTaxesDebt().compareTo(new BigDecimal("0")) > 0)
                .map(debtor -> {
                    BigDecimal recalculatedDebt = debtor.getTaxesDebt().multiply(new BigDecimal("1.05"));
                    BigDecimal roundedDebts = recalculatedDebt.setScale(1, RoundingMode.HALF_UP);
                    return debtor.withTaxesDebt(roundedDebts);
                })
                .forEach(updatedDebtor -> ownerRepository.update(updatedDebtor.getId(), updatedDebtor));
    }

    public boolean addNewOwner(Owner owner) {
        if (Objects.isNull(owner)) {
            throw new NoSuchOwnerException("This owner does not exists");
        }

        return this.ownerRepository.save(owner);
    }

    public boolean updateInfo(int id, Owner ownerToUpdate) {
        if (Objects.isNull(ownerToUpdate)) {
            throw new UpdateOwnerFailedException("Updating owner was failed");
        }

        Owner owner = this.ownerRepository.findById(id);
        if (Objects.isNull(owner)) {
            throw new NoSuchOwnerException("This owner does not exists");
        }

        return this.ownerRepository.update(id, ownerToUpdate);
    }

    public boolean removeOwner(int id) {
        return this.ownerRepository.remove(id);
    }

    public BigDecimal countTaxObligation(int id) {
        Owner owner = this.ownerRepository.findById(id);

        if (Objects.isNull(owner)) {
            throw new NoSuchOwnerException("This owner does not exists");
        }

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
        if (Objects.isNull(properties)) {
            throw new PropertyNotFoundException("The list of property does not exist");
        }

        List<TaxRate> taxRates = this.taxRateService.getAll();
        if (taxRates.size() != TAXES_RATE_NUMBER) {
            throw new InvalidTaxRateNumberException("Invalid number of tax rates. Current size is " + taxRates.size());
        }

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
