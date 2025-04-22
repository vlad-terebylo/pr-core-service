package com.tvo.propertyregister.integration;

import com.tvo.propertyregister.exception.NoSuchOwnerException;
import com.tvo.propertyregister.exception.PropertyNotFoundException;
import com.tvo.propertyregister.integration.config.repository.OwnerTestRepository;
import com.tvo.propertyregister.integration.config.repository.PropertyTestRepository;
import com.tvo.propertyregister.model.owner.FamilyStatus;
import com.tvo.propertyregister.model.owner.Owner;
import com.tvo.propertyregister.model.property.Property;
import com.tvo.propertyregister.model.property.PropertyCondition;
import com.tvo.propertyregister.model.property.PropertyType;
import com.tvo.propertyregister.service.OwnerService;
import com.tvo.propertyregister.service.PropertyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PropertyServiceIntegrationTests extends AbstractServiceTest {

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private PropertyTestRepository propertyTestRepository;

    @Autowired
    private OwnerService ownerService;

    @Autowired
    private OwnerTestRepository ownerRepository;

    private static final Property PROPERTY_1 = new Property(
            1, PropertyType.FLAT, "Prague", "Heroev Street 24",
            70, 3, new BigDecimal("500000"),
            LocalDate.of(2020, 4, 10),
            LocalDate.of(2012, 1, 9),
            PropertyCondition.GOOD);

    private static final Property PROPERTY_2 = new Property(2, PropertyType.HOUSE, "Prague", "Boris Niemcov Street 220",
            150, 5, new BigDecimal("750000"),
            LocalDate.of(2020, 4, 10),
            LocalDate.of(2012, 1, 9),
            PropertyCondition.GOOD);

    private static final Owner OWNER = new Owner(2, "Linda", "Johnson",
            39, FamilyStatus.MARRIED,
            true, "lindajohnson@gmail.com",
            "+123456789",
            LocalDate.of(1986, 8, 9),
            new BigDecimal("0"), List.of(PROPERTY_1));

    private static final int INVALID_ID = -1;

    @BeforeEach
    void cleanUp() {
        propertyTestRepository.clear();
        ownerRepository.clear();
    }

    @Test
    void should_get_all_properties_by_owner_id() {
        ownerService.addNewOwner(OWNER);
        propertyService.save(OWNER.getId(), PROPERTY_2);

        List<Property> actualProperties = propertyService.getAll(OWNER.getId());

        assertEquals(List.of(PROPERTY_1, PROPERTY_2), actualProperties);
    }

    @Test
    void should_get_all_properties_by_owner_id_if_the_id_is_wrong() {
        assertThrows(NoSuchOwnerException.class, () -> propertyService.getAll(INVALID_ID));
    }

    @Test
    void should_get_all_properties_by_owner_id_if_owner_does_not_have_property() {
        Owner owner = new Owner(1, "Frank", "John",
                30, FamilyStatus.SINGLE,
                false, "frankjohn@gmail.com",
                "+456987123",
                LocalDate.of(1994, 5, 9),
                new BigDecimal("10000"), List.of());

        boolean result = ownerService.addNewOwner(owner);

        assertTrue(result);
        assertEquals(List.of(), owner.getProperties());
    }

    @Test
    void should_add_new_property_to_certain_owner() {
        ownerService.addNewOwner(OWNER);

        boolean result = propertyService.save(OWNER.getId(), PROPERTY_2);
        assertTrue(result);
    }

    @Test
    void should_not_add_new_property_if_id_is_wrong() {
        assertThrows(NoSuchOwnerException.class, () -> propertyService.save(INVALID_ID, PROPERTY_2));
    }

    @Test
    void should_not_add_new_property_if_property_is_null() {
        assertThrows(PropertyNotFoundException.class, () -> propertyService.save(1, null));
    }

    @Test
    void should_update_property_for_certain_owner() {
        Owner owner = new Owner(2, "Linda", "Johnson",
                39, FamilyStatus.MARRIED,
                true, "lindajohnson@gmail.com",
                "+123456789",
                LocalDate.of(1986, 8, 9),
                new BigDecimal("0"), new ArrayList<>());

        ownerService.addNewOwner(owner);

        int ownerId = owner.getId();

        propertyService.save(ownerId, PROPERTY_1);
        propertyService.save(ownerId, PROPERTY_2);

        PROPERTY_2.setCity("Kyiv");
        PROPERTY_2.setAddress("K-street");
        PROPERTY_2.setNumberOfRooms(4);

        propertyService.update(ownerId, PROPERTY_2.getId(), PROPERTY_2);

        List<Property> ownerProperties = propertyService.getAll(ownerId);
        ownerProperties.forEach(System.out::println);

        assertEquals(List.of(PROPERTY_1, PROPERTY_2), ownerProperties);
    }


    @Test
    void should_not_update_property_for_certain_owner_if_owner_id_is_wrong() {
        assertThrows(NoSuchOwnerException.class, () -> propertyService.update(INVALID_ID, PROPERTY_2.getId(), PROPERTY_2));
    }

    @Test
    void should_not_update_property_for_certain_owner_if_property_id_is_wrong() {
        ownerService.addNewOwner(OWNER);

        assertThrows(PropertyNotFoundException.class, () -> propertyService.update(OWNER.getId(), INVALID_ID, PROPERTY_1));
    }

    @Test
    void should_not_update_property_for_certain_owner_if_property_is_null() {
        assertThrows(PropertyNotFoundException.class, () -> propertyService.update(1, PROPERTY_2.getId(), null));
    }

    @Test
    void should_not_remove_property_for_certain_owner_if_property_id_is_null() {
        ownerService.addNewOwner(OWNER);

        assertThrows(PropertyNotFoundException.class, () -> propertyService.remove(OWNER.getId(), INVALID_ID));
    }

    @Test
    void should_remove_property_for_certain_owner() {
        ownerService.addNewOwner(OWNER);
        boolean isAdded = propertyService.save(OWNER.getId(), PROPERTY_2);
        assertTrue(isAdded);

        boolean isRemoved = propertyService.remove(OWNER.getId(), PROPERTY_2.getId());
        assertTrue(isRemoved);
    }

    @Test
    void should_not_remove_property_for_certain_owner_if_owner_id_is_wrong() {
        assertThrows(NoSuchOwnerException.class, () -> propertyService.remove(INVALID_ID, 1));
    }
}
