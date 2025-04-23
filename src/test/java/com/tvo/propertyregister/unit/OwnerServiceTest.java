package com.tvo.propertyregister.unit;

import com.tvo.propertyregister.exception.NoSuchOwnerException;
import com.tvo.propertyregister.exception.UpdateOwnerFailedException;
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

import static org.junit.jupiter.api.Assertions.*;
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

    private static final Owner OWNER_2 = new Owner(3, "Carel", "Capek",
            36, FamilyStatus.SINGLE,
            true, "carelcapek@gmail.com",
            "+420098465743",
            LocalDate.of(1994, 8, 9),
            new BigDecimal("0"), List.of(PROPERTY_HOUSE));

    private static final Owner DEBTOR = new Owner(2, "Linda", "Johnson",
            31, FamilyStatus.MARRIED,
            true, "lindajohnson@gmail.com",
            "+789456147",
            LocalDate.of(1993, 7, 17),
            new BigDecimal("10000.0"), List.of(PROPERTY_FLAT));

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
        when(ownerRepository.findAll()).thenReturn(List.of(OWNER));

        List<Owner> result = ownerService.getAllOwners();

        assertEquals(List.of(OWNER), result);
    }

    @Test
    public void should_return_empty_list_if_no_owners() {
        when(ownerRepository.findAll()).thenReturn(List.of());

        List<Owner> result = ownerService.getAllOwners();

        assertEquals(List.of(), result);
    }

    @Test
    public void should_return_owner_by_id() {
        when(ownerRepository.findById(OWNER.getId())).thenReturn(OWNER);

        Owner result = ownerService.getOwnerById(OWNER.getId());

        assertEquals(OWNER, result);
    }

    @Test
    public void should_not_return_owner_by_id_if_id_is_wrong() {
        int wrongId = -1;
        when(ownerRepository.findById(wrongId)).thenThrow(NoSuchOwnerException.class);

        assertThrows(NoSuchOwnerException.class, () -> ownerService.getOwnerById(wrongId));
    }

    @Test
    public void should_return_all_debtors() {
        when(ownerRepository.findDebtors()).thenReturn(List.of(DEBTOR));

        List<Owner> result = ownerService.findDebtors();

        assertEquals(List.of(DEBTOR), result);
    }

    @Test
    public void should_return_empty_list_if_there_are_no_debtors() {
        when(ownerRepository.findDebtors()).thenReturn(List.of());

        List<Owner> result = ownerService.findDebtors();

        assertEquals(List.of(), result);
    }

    @Test
    void should_recalculate_debt_for_debtors() {
        List<Owner> allDebtors = List.of(DEBTOR);
        Owner expectedDebtor = new Owner(2, "Linda", "Johnson",
                31, FamilyStatus.MARRIED,
                true, "lindajohnson@gmail.com",
                "+789456147",
                LocalDate.of(1993, 7, 17),
                new BigDecimal("10500.0"), List.of(PROPERTY_FLAT));

        when(ownerRepository.findDebtors()).thenReturn(allDebtors);

        ownerService.recountDebtForDebtors();

        verify(ownerRepository, times(1)).findDebtors();
        verify(ownerRepository, times(1)).update(DEBTOR.getId(), expectedDebtor);
    }

    @Test
    void should_not_recalculate_debt_is_the_owner_does_not_have_debts() {
        when(ownerRepository.findDebtors()).thenReturn(List.of());

        ownerService.recountDebtForDebtors();

        verify(ownerRepository, times(1)).findDebtors();
        verify(ownerRepository, never()).update(anyInt(), any());
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
    public void should_not_update_non_existing_owner() {
        int wrongId = -1;
        when(ownerRepository.update(wrongId, OWNER)).thenThrow(UpdateOwnerFailedException.class);

        assertThrows(UpdateOwnerFailedException.class, () -> ownerService.updateInfo(wrongId, OWNER));
    }


    @Test
    public void should_delete_owner() {
        ownerService.removeOwner(OWNER.getId());

        verify(ownerRepository, times(1)).remove(OWNER.getId());
    }

    @Test
    void should_not_delete_owner_if_owner_does_not_exists() {
        int wrongId = -1;
        when(ownerRepository.remove(wrongId)).thenReturn(false);

        boolean result = ownerService.removeOwner(wrongId);

        assertFalse(result);
    }

    @Test
    public void should_count_base_tax_for_owner_with_no_leeway() {

        BigDecimal expectedBaseTax = new BigDecimal("3320");

        // when
        when(taxRateService.getAll()).thenReturn(List.of(
                TAX_RATE_FLAT,
                TAX_RATE_HOUSE,
                TAX_RATE_OFFICE));

        when(ownerService.getOwnerById(OWNER.getId())).thenReturn(OWNER);

        BigDecimal baseTaxResult = ownerService.countTaxObligation(OWNER.getId());

        // then
        assertEquals(expectedBaseTax, baseTaxResult);
    }

    @Test
    public void should_count_base_tax_for_owner_with_multiple_leeway() {
        // given
        BigDecimal expectedBaseTax = new BigDecimal("336.0");

        // when
        when(taxRateService.getAll()).thenReturn(List.of(
                TAX_RATE_FLAT,
                TAX_RATE_HOUSE,
                TAX_RATE_OFFICE));

        when(ownerService.getOwnerById(DEBTOR.getId())).thenReturn(DEBTOR);

        BigDecimal baseTaxResult = ownerService.countTaxObligation(DEBTOR.getId());

        // then
        assertEquals(expectedBaseTax, baseTaxResult);
    }

    @Test
    public void should_count_base_tax_for_single_owner_and_with_children() {
        BigDecimal expectedTaxObligation = new BigDecimal("1120.0");

        when(taxRateService.getAll()).thenReturn(List.of(
                TAX_RATE_FLAT,
                TAX_RATE_HOUSE,
                TAX_RATE_OFFICE
        ));

        when(ownerService.getOwnerById(OWNER_2.getId())).thenReturn(OWNER_2);

        BigDecimal taxObligationResult = ownerService.countTaxObligation(OWNER_2.getId());

        assertEquals(expectedTaxObligation, taxObligationResult);
    }

    @Test
    public void should_count_tax_obligations_for_married_owner_without_children() {
        BigDecimal expectedTaxObligations = new BigDecimal("378.0");

        Owner owner = new Owner(4, "Linda", "Johnson",
                31, FamilyStatus.MARRIED,
                false, "lindajohnson@gmail.com",
                "+789456147",
                LocalDate.of(1993, 7, 17),
                new BigDecimal("10000"), List.of(PROPERTY_FLAT));

        when(taxRateService.getAll()).thenReturn(List.of(
                TAX_RATE_FLAT,
                TAX_RATE_HOUSE,
                TAX_RATE_OFFICE
        ));

        when(ownerRepository.findById(owner.getId())).thenReturn(owner);

        BigDecimal taxObligationResult = ownerService.countTaxObligation(owner.getId());

        assertEquals(expectedTaxObligations, taxObligationResult);
    }
}
