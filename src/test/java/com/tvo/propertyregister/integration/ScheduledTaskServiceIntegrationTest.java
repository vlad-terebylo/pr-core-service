package com.tvo.propertyregister.integration;

import com.tvo.propertyregister.integration.config.repository.OwnerTestRepository;
import com.tvo.propertyregister.model.owner.FamilyStatus;
import com.tvo.propertyregister.model.owner.Owner;
import com.tvo.propertyregister.model.property.Property;
import com.tvo.propertyregister.model.property.PropertyCondition;
import com.tvo.propertyregister.model.property.PropertyType;
import com.tvo.propertyregister.scheduler.ScheduledTaskService;
import com.tvo.propertyregister.service.OwnerService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScheduledTaskServiceIntegrationTest extends AbstractServiceTest {

    private static final Property FLAT = new Property(
            1, PropertyType.FLAT, "Prague", "Heroev Street 24",
            70, 3, new BigDecimal("500000"),
            LocalDate.of(2020, 4, 10),
            LocalDate.of(2012, 1, 9),
            PropertyCondition.GOOD);

    private static final Property FIRST_HOUSE = new Property(2, PropertyType.HOUSE, "Prague", "Boris Niemcov Street 220",
            150, 5, new BigDecimal("750000"),
            LocalDate.of(2020, 4, 10),
            LocalDate.of(2012, 1, 9),
            PropertyCondition.GOOD);
    private static final Property SECOND_HOUSE = new Property(3, PropertyType.HOUSE, "Prague", "Evropska 6",
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

    @Autowired
    private ScheduledTaskService scheduledTaskService;

    @Autowired
    private OwnerService ownerService;

    @Autowired
    private OwnerTestRepository ownerTestRepository;

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

    @AfterEach
    public void cleanUp() {
        ownerTestRepository.clear();
    }

    @Test
    void should_recount_debt_for_debtors_when_no_debtors() {
        ownerService.addNewOwner(SINGLE_OWNER_WITHOUT_CHILDREN);

        scheduledTaskService.recountDebtForDebtors();

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
                new BigDecimal("10000"), List.of(SECOND_HOUSE));

        ownerService.addNewOwner(debtor);

        scheduledTaskService.recountDebtForDebtors();

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
                new BigDecimal("10000"), List.of(SECOND_HOUSE));

        Owner debtor2 = new Owner(2, "Alice", "Wonder",
                28, FamilyStatus.SINGLE,
                false, "alicewonder@gmail.com",
                "+111111111", LocalDate.of(1997, 1, 1),
                new BigDecimal("20000"), List.of(FIRST_HOUSE));

        ownerService.addNewOwner(debtor);
        ownerService.addNewOwner(debtor2);

        scheduledTaskService.recountDebtForDebtors();

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
                new BigDecimal("10000"), List.of(SECOND_HOUSE));

        ownerService.addNewOwner(SINGLE_OWNER_WITHOUT_CHILDREN);
        ownerService.addNewOwner(debtor);

        scheduledTaskService.recountDebtForDebtors();

        Owner nonDebtor = ownerService.getOwnerById(SINGLE_OWNER_WITHOUT_CHILDREN.getId());
        assertEquals(BigDecimal.ZERO, nonDebtor.getTaxesDebt());

        Owner updatedDebtor = ownerService.getOwnerById(debtor.getId());
        BigDecimal expectedDebt = new BigDecimal("10500.0");
        assertEquals(expectedDebt, updatedDebtor.getTaxesDebt());
    }
}
