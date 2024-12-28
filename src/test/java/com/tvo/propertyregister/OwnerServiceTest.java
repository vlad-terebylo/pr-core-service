package com.tvo.propertyregister;

import com.tvo.propertyregister.model.TaxRate;
import com.tvo.propertyregister.model.owner.FamilyStatus;
import com.tvo.propertyregister.model.owner.Owner;
import com.tvo.propertyregister.model.property.Property;
import com.tvo.propertyregister.model.property.PropertyCondition;
import com.tvo.propertyregister.model.property.PropertyType;
import com.tvo.propertyregister.repository.OwnerRepository;
import com.tvo.propertyregister.service.OwnerService;
import com.tvo.propertyregister.service.TaxRateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OwnerServiceTest {

    private static final Property PROPERTY_FLAT = new Property(
            1, PropertyType.FLAT, "Prague", "Heroev Street 24",
            70, 3, new BigDecimal("500000"),
            LocalDate.of(2020, 4, 10),
            LocalDate.of(2012, 1, 9),
            PropertyCondition.GOOD);

    private static final Property PROPERTY_HOUSE = new Property(
            2, PropertyType.HOUSE, "Prague", "Trojmezni 90",
            200, 5, new BigDecimal("1000000"),
            LocalDate.of(2020, 4, 10),
            LocalDate.of(2012, 1, 9),
            PropertyCondition.MEDIUM);

    private static final Property PROPERTY_OFFICE = new Property(
            3, PropertyType.OFFICE, "Prague", "Heroev Street 1",
            100, 7, new BigDecimal("250000"),
            LocalDate.of(2020, 4, 10),
            LocalDate.of(2012, 1, 9),
            PropertyCondition.BAD_REPAIR);

    private static final Owner OWNER = new Owner(1, "John", "Smith",
            30, FamilyStatus.SINGLE,
            false, "johnsmith@gmail.com",
            "+456987123",
            LocalDate.of(1994, 8, 9),
            new BigDecimal("0"), List.of(PROPERTY_FLAT, PROPERTY_HOUSE, PROPERTY_OFFICE));

    private static final Owner DEBTOR = new Owner(2, "Linda", "Johnson",
            31, FamilyStatus.MARRIED,
            true, "lindajohnson@gmail.com",
            "+789456147",
            LocalDate.of(1993, 7, 17),
            new BigDecimal("10000"), List.of(PROPERTY_FLAT));

    private static final TaxRate TAX_RATE_FLAT = new TaxRate(1, PropertyType.FLAT, new BigDecimal("6"));
    private static final TaxRate TAX_RATE_HOUSE = new TaxRate(2, PropertyType.HOUSE, new BigDecimal("8"));
    private static final TaxRate TAX_RATE_OFFICE = new TaxRate(3, PropertyType.OFFICE, new BigDecimal("13"));

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private TaxRateService taxRateService;

    @InjectMocks
    private OwnerService ownerService;


    @Test
    public void should_return_all_owners() {
        when(ownerRepository.getAllOwners()).thenReturn(List.of(OWNER));

        List<Owner> result = ownerService.getAllOwners();

        assertEquals(List.of(OWNER), result);
    }

    @Test
    public void should_return_owner_by_id() {
        when(ownerRepository.findById(OWNER.getId())).thenReturn(OWNER);

        Owner result = ownerService.getOwnerById(OWNER.getId());

        assertEquals(OWNER, result);
    }

    @Test
    public void should_return_all_debtors() {
        when(ownerRepository.findDebtors()).thenReturn(List.of(DEBTOR));

        List<Owner> result = ownerService.findDebtors();

        assertEquals(List.of(DEBTOR), result);

    }

    @Test
    public void should_add_new_owner() {
        ownerService.addNewOwner(OWNER);

        verify(ownerRepository, times(1)).save(OWNER);
    }

    @Test
    public void should_update_owner_info() {
        ownerService.updateInfo(OWNER.getId(), OWNER);

        verify(ownerRepository, times(1)).update(OWNER.getId(), OWNER);
    }

    @Test
    public void should_delete_owner() {
        ownerService.removeOwner(OWNER.getId());

        verify(ownerRepository, times(1)).remove(OWNER.getId());
    }

    @Test
    public void should_count_base_tax_for_owner_having_multiple_properties_with_no_leeway() {

        BigDecimal expectedBaseTax = new BigDecimal("3320");

        // when
        when(taxRateService.getAll()).thenReturn(List.of(
                TAX_RATE_FLAT,
                TAX_RATE_HOUSE,
                TAX_RATE_OFFICE));

        when(ownerRepository.findById(OWNER.getId())).thenReturn(OWNER);

        BigDecimal baseTaxResult = ownerService.countTaxObligation(OWNER.getId());

        // then
        assertEquals(expectedBaseTax, baseTaxResult);
    }

    @Test
    public void should_count_base_tax_for_owner_having_single_property_with_multiple_leeway() {
        // given
        BigDecimal expectedBaseTax = new BigDecimal("336.0");

        // when
        when(taxRateService.getAll()).thenReturn(List.of(
                TAX_RATE_FLAT,
                TAX_RATE_HOUSE,
                TAX_RATE_OFFICE));

        when(ownerRepository.findById(DEBTOR.getId())).thenReturn(DEBTOR);

        BigDecimal baseTaxResult = ownerService.countTaxObligation(DEBTOR.getId());

        // then
        assertEquals(expectedBaseTax, baseTaxResult);
    }

}
