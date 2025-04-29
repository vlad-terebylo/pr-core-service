package com.tvo.propertyregister.integration;

import com.tvo.propertyregister.exception.InvalidTaxRateNumberException;
import com.tvo.propertyregister.exception.NoSuchOwnerException;
import com.tvo.propertyregister.exception.PropertyNotFoundException;
import com.tvo.propertyregister.exception.UpdateOwnerFailedException;
import com.tvo.propertyregister.integration.config.repository.OwnerTestRepository;
import com.tvo.propertyregister.integration.config.repository.TaxRateTestRepository;
import com.tvo.propertyregister.model.TaxRate;
import com.tvo.propertyregister.model.owner.FamilyStatus;
import com.tvo.propertyregister.model.owner.Owner;
import com.tvo.propertyregister.model.property.Property;
import com.tvo.propertyregister.model.property.PropertyCondition;
import com.tvo.propertyregister.model.property.PropertyType;
import com.tvo.propertyregister.service.OwnerService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class OwnerServiceIntegrationTests extends AbstractServiceTest {

    @Autowired
    private OwnerService ownerService;

    @Autowired
    private OwnerTestRepository ownerTestRepository;

    @Autowired
    private TaxRateTestRepository taxRateTestRepository;

    private static final int INVALID_ID = -1;

    private static final Property FLAT = new Property(
            1, PropertyType.FLAT, "Prague", "Heroev Street 24",
            70, 3, new BigDecimal("500000"),
            LocalDate.of(2020, 4, 10),
            LocalDate.of(2012, 1, 9),
            PropertyCondition.GOOD);

    private static final Property HOUSE_1 = new Property(2, PropertyType.HOUSE, "Prague", "Boris Niemcov Street 220",
            150, 5, new BigDecimal("750000"),
            LocalDate.of(2020, 4, 10),
            LocalDate.of(2012, 1, 9),
            PropertyCondition.GOOD);
    private static final Property HOUSE_2 = new Property(3, PropertyType.HOUSE, "Prague", "Evropska 6",
            300, 10, new BigDecimal("1000000"),
            LocalDate.of(2023, 4, 10),
            LocalDate.of(2023, 1, 9),
            PropertyCondition.GOOD);

    private static final Owner SINGLE_OWNER_WITHOUT_CHILDREN = new Owner(1, "John", "Smith",
            30, FamilyStatus.SINGLE,
            false, "johnsmith@gmail.com",
            "+456987123",
            LocalDate.of(1994, 8, 9),
            new BigDecimal("0"), List.of(FLAT));

    private static final Owner MARRIED_OWNER_WITH_CHILDREN = new Owner(2, "Linda", "Johnson",
            39, FamilyStatus.MARRIED,
            true, "lindajohnson@gmail.com",
            "+123456789",
            LocalDate.of(1986, 8, 9),
            new BigDecimal("0"), List.of(HOUSE_1));

    private static final Owner DEBTOR = new Owner(3, "Frank", "John",
            30, FamilyStatus.SINGLE,
            false, "frankjohn@gmail.com",
            "+456987123",
            LocalDate.of(1994, 5, 9),
            new BigDecimal("10000.0"), List.of(HOUSE_2));

    private static final List<TaxRate> taxRates = List.of(
            new TaxRate(1, PropertyType.FLAT, new BigDecimal("6")),
            new TaxRate(2, PropertyType.HOUSE, new BigDecimal("8")),
            new TaxRate(3, PropertyType.OFFICE, new BigDecimal("13"))
    );

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.host", MONGO_DB_CONTAINER::getHost);
        registry.add("spring.data.mongodb.port", MONGO_DB_CONTAINER::getFirstMappedPort);
    }

    @BeforeAll
    public static void startContainer() {
        MONGO_DB_CONTAINER.start();
    }

    @AfterAll
    public static void stopContainer() {
        MONGO_DB_CONTAINER.stop();
    }

    @BeforeEach
    public void init() {
        taxRateTestRepository.initTaxRates();
    }

    @AfterEach
    public void cleanUp() {
        ownerTestRepository.clear();
        taxRateTestRepository.clear();
    }

    @Test
    void should_return_all_owners_when_the_list_is_empty() {
        List<Owner> result = ownerService.getAllOwners();
        assertEquals(List.of(), result);
    }

    @Test
    void should_return_all_owners_when_the_list_is_not_empty() {
        ownerService.addNewOwner(SINGLE_OWNER_WITHOUT_CHILDREN);

        List<Owner> result = ownerService.getAllOwners();
        assertEquals(List.of(SINGLE_OWNER_WITHOUT_CHILDREN), result);
    }

    @Test
    void should_return_owner_by_id() {
        ownerService.addNewOwner(SINGLE_OWNER_WITHOUT_CHILDREN);
        Owner createdOwner = ownerService.getOwnerById(SINGLE_OWNER_WITHOUT_CHILDREN.getId());

        assertEquals(SINGLE_OWNER_WITHOUT_CHILDREN, createdOwner);
    }

    @Test
    void should_return_owner_by_id_if_id_is_wrong() {
        assertThrows(NoSuchOwnerException.class, () -> ownerService.getOwnerById(INVALID_ID));
    }

    @Test
    void should_return_all_debtors_when_the_list_is_not_empty_but_no_debtors() {
        ownerService.addNewOwner(SINGLE_OWNER_WITHOUT_CHILDREN);

        List<Owner> result = ownerService.findDebtors();

        assertEquals(List.of(), result);
    }

    @Test
    void should_return_all_debtors_when_the_list_is_empty() {
        List<Owner> result = ownerService.findDebtors();

        assertEquals(List.of(), result);
    }

    @Test
    void should_return_all_debtors_when_the_list_is_not_empty_and_debtors_in_list() {
        ownerService.addNewOwner(SINGLE_OWNER_WITHOUT_CHILDREN);
        ownerService.addNewOwner(DEBTOR);

        List<Owner> result = ownerService.findDebtors();

        assertEquals(List.of(DEBTOR), result);
    }

    @Test
    void should_return_all_debtors_when_there_is_only_debtors_in_list() {
        ownerService.addNewOwner(DEBTOR);

        List<Owner> result = ownerService.findDebtors();

        assertEquals(List.of(DEBTOR), result);
    }

    @Test
    void should_recount_debt_for_debtors_when_no_debtors() {
        ownerService.addNewOwner(SINGLE_OWNER_WITHOUT_CHILDREN);

        ownerService.recountDebtForDebtors();

        Owner nonDebtor = ownerService.getOwnerById(SINGLE_OWNER_WITHOUT_CHILDREN.getId());
        assertEquals(BigDecimal.ZERO, nonDebtor.getTaxesDebt());
    }

    @Test
    void should_recount_debt_for_debtors_when_in_list_single_debtor() {
        Owner debtor = new Owner(1, "Frank", "John",
                30, FamilyStatus.SINGLE,
                false, "frankjohn@gmail.com",
                "+456987123",
                LocalDate.of(1994, 5, 9),
                new BigDecimal("10000"), List.of(HOUSE_2));

        ownerService.addNewOwner(debtor);

        ownerService.recountDebtForDebtors();

        Owner updatedDebtor = ownerService.getOwnerById(debtor.getId());
        BigDecimal expectedDebt = new BigDecimal("10500.0");

        assertEquals(expectedDebt, updatedDebtor.getTaxesDebt());
    }

    @Test
    void should_recount_debt_for_debtors_when_in_list_two_or_more_debtors() {
        Owner debtor = new Owner(1, "Frank", "John",
                30, FamilyStatus.SINGLE,
                false, "frankjohn@gmail.com",
                "+456987123",
                LocalDate.of(1994, 5, 9),
                new BigDecimal("10000"), List.of(HOUSE_2));

        Owner debtor2 = new Owner(2, "Alice", "Wonder",
                28, FamilyStatus.SINGLE,
                false, "alicewonder@gmail.com",
                "+111111111", LocalDate.of(1997, 1, 1),
                new BigDecimal("20000"), List.of(HOUSE_1));

        ownerService.addNewOwner(debtor);
        ownerService.addNewOwner(debtor2);

        ownerService.recountDebtForDebtors();

        Owner updatedDebtor1 = ownerService.getOwnerById(debtor.getId());
        BigDecimal expectedDebt1 = new BigDecimal("10500.0");
        assertEquals(expectedDebt1, updatedDebtor1.getTaxesDebt());

        Owner updatedDebtor2 = ownerService.getOwnerById(debtor2.getId());
        BigDecimal expectedDebt2 = new BigDecimal("21000.0");
        assertEquals(expectedDebt2, updatedDebtor2.getTaxesDebt());
    }

    @Test
    void should_recount_debt_for_debtors_when_in_list_debtors_and_owners_without_debts() {
        Owner debtor = new Owner(1, "Frank", "John",
                30, FamilyStatus.SINGLE,
                false, "frankjohn@gmail.com",
                "+456987123",
                LocalDate.of(1994, 5, 9),
                new BigDecimal("10000"), List.of(HOUSE_2));

        ownerService.addNewOwner(SINGLE_OWNER_WITHOUT_CHILDREN);
        ownerService.addNewOwner(debtor);

        ownerService.recountDebtForDebtors();

        Owner nonDebtor = ownerService.getOwnerById(SINGLE_OWNER_WITHOUT_CHILDREN.getId());
        assertEquals(BigDecimal.ZERO, nonDebtor.getTaxesDebt());

        Owner updatedDebtor = ownerService.getOwnerById(debtor.getId());
        BigDecimal expectedDebt = new BigDecimal("10500.0");
        assertEquals(expectedDebt, updatedDebtor.getTaxesDebt());
    }

    @Test
    void should_not_recalculate_debt_if_debt_is_zero() {
        ownerService.addNewOwner(SINGLE_OWNER_WITHOUT_CHILDREN);

        BigDecimal expectedDept = new BigDecimal("0");

        assertEquals(expectedDept, SINGLE_OWNER_WITHOUT_CHILDREN.getTaxesDebt());
    }

    @Test
    void should_save_new_owner() {
        boolean isAdded = ownerService.addNewOwner(SINGLE_OWNER_WITHOUT_CHILDREN);
        Owner result = ownerService.getOwnerById(SINGLE_OWNER_WITHOUT_CHILDREN.getId());

        assertTrue(isAdded);
        assertEquals(SINGLE_OWNER_WITHOUT_CHILDREN, result);
    }

    @Test
    void should_not_save_new_owner_if_owner_is_null(){
        assertThrows(NoSuchOwnerException.class, () -> ownerService.addNewOwner(null));
    }

    @Test
    void should_update_owner_info() {
        int id = 1;
        Owner owner = new Owner(id, "John", "Smith",
                30, FamilyStatus.MARRIED,
                false, "johnsmith@gmail.com",
                "+456987123",
                LocalDate.of(1994, 8, 9),
                new BigDecimal("0"), List.of(FLAT));

        ownerService.addNewOwner(owner);

        assertEquals(owner, ownerService.getOwnerById(id));

        owner.setFamilyStatus(FamilyStatus.MARRIED);
        owner.setLastName("Faith");

        boolean result = ownerService.updateInfo(id, owner);

        assertTrue(result);
        assertEquals(owner, ownerService.getOwnerById(id));
    }

    @Test
    void should_not_update_owner_and_throw_exception_if_does_not_exist() {
        assertThrows(UpdateOwnerFailedException.class, () -> ownerService.updateInfo(1, null));
    }

    @Test
    void should_not_update_owner_and_throw_exception_if_not_found_by_id() {
        assertThrows(NoSuchOwnerException.class, () -> ownerService.updateInfo(INVALID_ID, SINGLE_OWNER_WITHOUT_CHILDREN));
    }

    @Test
    void should_remove_owner() {
        ownerService.addNewOwner(SINGLE_OWNER_WITHOUT_CHILDREN);
        Owner gotOwner = ownerService.getOwnerById(SINGLE_OWNER_WITHOUT_CHILDREN.getId());

        assertEquals(SINGLE_OWNER_WITHOUT_CHILDREN, gotOwner);

        boolean result = ownerService.removeOwner(SINGLE_OWNER_WITHOUT_CHILDREN.getId());

        assertTrue(result);
        assertEquals(List.of(), ownerService.getAllOwners());
    }

    @Test
    void should_return_false_if_nothing_to_remove() {
        boolean result = ownerService.removeOwner(1);

        assertFalse(result);
    }

    @Test
    void should_count_tax_obligations_no_leeway() {
        ownerService.addNewOwner(SINGLE_OWNER_WITHOUT_CHILDREN);

        assertEquals(SINGLE_OWNER_WITHOUT_CHILDREN, ownerService.getOwnerById(SINGLE_OWNER_WITHOUT_CHILDREN.getId()));
        assertEquals(taxRateTestRepository.findAll(), taxRates);

        BigDecimal expectedTaxObligation = new BigDecimal("420");
        BigDecimal realTaxObligation = ownerService.countTaxObligation(SINGLE_OWNER_WITHOUT_CHILDREN.getId());

        assertEquals(expectedTaxObligation, realTaxObligation);
    }

    @Test
    void should_count_tax_obligations_with_multiple_leeway() {
        ownerService.addNewOwner(MARRIED_OWNER_WITH_CHILDREN);

        assertEquals(ownerService.getOwnerById(MARRIED_OWNER_WITH_CHILDREN.getId()), MARRIED_OWNER_WITH_CHILDREN);
        assertEquals(taxRateTestRepository.findAll(), taxRates);

        BigDecimal expectedTaxObligation = new BigDecimal("960.0");
        BigDecimal realTaxObligation = ownerService.countTaxObligation(MARRIED_OWNER_WITH_CHILDREN.getId());

        assertEquals(expectedTaxObligation, realTaxObligation);
    }

    @Test
    void should_count_tax_obligations_if_owner_is_married_and_dont_has_children() {
        int id = 1;
        Owner owner = new Owner(id, "John", "Smith",
                30, FamilyStatus.MARRIED,
                false, "johnsmith@gmail.com",
                "+456987123",
                LocalDate.of(1994, 8, 9),
                new BigDecimal("0"), List.of(FLAT));

        ownerService.addNewOwner(owner);

        assertEquals(ownerService.getOwnerById(id), owner);
        assertEquals(taxRateTestRepository.findAll(), taxRates);

        BigDecimal expectedTaxObligation = new BigDecimal("378.0");
        BigDecimal realTaxObligation = ownerService.countTaxObligation(id);

        assertEquals(expectedTaxObligation, realTaxObligation);
    }

    @Test
    void should_count_tax_obligations_if_owner_has_children_but_is_not_married() {
        int id = 1;

        Owner owner = new Owner(id, "John", "Smith",
                30, FamilyStatus.SINGLE,
                true, "johnsmith@gmail.com",
                "+456987123",
                LocalDate.of(1994, 8, 9),
                new BigDecimal("0"), List.of(FLAT));
        ownerService.addNewOwner(owner);

        assertEquals(ownerService.getOwnerById(id), owner);
        assertEquals(taxRateTestRepository.findAll(), taxRates);

        BigDecimal expectedTaxObligation = new BigDecimal("294.0");
        BigDecimal realTaxObligation = ownerService.countTaxObligation(id);

        assertEquals(expectedTaxObligation, realTaxObligation);
    }

    @Test
    void should_not_count_tax_obligations_if_owner_is_null(){
        assertThrows(NoSuchOwnerException.class, () -> ownerService.countTaxObligation(INVALID_ID));
    }

    @Test
    void should_throw_exception_if_property_is_null(){
        int id = 1;

        Owner owner = new Owner(id, "John", "Smith",
                30, FamilyStatus.SINGLE,
                true, "johnsmith@gmail.com",
                "+456987123",
                LocalDate.of(1994, 8, 9),
                new BigDecimal("0"), null);
        ownerService.addNewOwner(owner);

        assertThrows(PropertyNotFoundException.class, () -> ownerService.countTaxObligation(id));
    }

    @Test
    void should_throw_exception_if_tax_rate_number_is_invalid(){
        int id = 1;

        Owner owner = new Owner(id, "John", "Smith",
                30, FamilyStatus.SINGLE,
                true, "johnsmith@gmail.com",
                "+456987123",
                LocalDate.of(1994, 8, 9),
                new BigDecimal("0"), List.of(FLAT));
        ownerService.addNewOwner(owner);

        TaxRate flatTax = new TaxRate(4, PropertyType.FLAT, new BigDecimal("5.0"));
        TaxRate houseTax = new TaxRate(5, PropertyType.HOUSE, new BigDecimal("7.0"));

        taxRateTestRepository.insertTaxRate(flatTax);
        taxRateTestRepository.insertTaxRate(houseTax);

        assertThrows(InvalidTaxRateNumberException.class, () -> ownerService.countTaxObligation(id));
    }
}
