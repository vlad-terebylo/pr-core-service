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
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PropertyServiceIntegrationTests extends AbstractServiceTest {

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private OwnerService ownerService;

    @Autowired
    private PropertyTestRepository propertyTestRepository;

    @Autowired
    private OwnerTestRepository ownerTestRepository;

    private static final Property FIRST_PROPERTY = new Property(
            1, PropertyType.FLAT, "Prague", "Heroev Street 24",
            70, 3, new BigDecimal("500000"),
            LocalDate.of(2020, 4, 10),
            LocalDate.of(2012, 1, 9),
            PropertyCondition.GOOD);

    private static final Property SECOND_PROPERTY = new Property(2, PropertyType.HOUSE, "Prague", "Boris Niemcov Street 220",
            150, 5, new BigDecimal("750000"),
            LocalDate.of(2020, 4, 10),
            LocalDate.of(2012, 1, 9),
            PropertyCondition.GOOD);

    private static final Owner OWNER = new Owner(2, "Linda", "Johnson",
            39, FamilyStatus.MARRIED,
            true, "lindajohnson@gmail.com",
            "+123456789",
            LocalDate.of(1986, 8, 9),
            new BigDecimal("0"), List.of(FIRST_PROPERTY));

    private static final int INVALID_ID = -1;

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
    void cleanUp() {
        propertyTestRepository.clear();
        ownerTestRepository.clear();
    }

    @Test
    void should_get_all_properties_by_owner_id() {
        ownerService.addNewOwner(OWNER);
        propertyService.save(OWNER.getId(), SECOND_PROPERTY);

        List<Property> actualProperties = propertyService.getAll(OWNER.getId());

        assertEquals(List.of(FIRST_PROPERTY, SECOND_PROPERTY), actualProperties);
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

        boolean result = propertyService.save(OWNER.getId(), SECOND_PROPERTY);
        assertTrue(result);
    }

    @Test
    void should_not_add_new_property_if_id_is_wrong() {
        assertThrows(NoSuchOwnerException.class, () -> propertyService.save(INVALID_ID, SECOND_PROPERTY));
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

        propertyService.save(ownerId, FIRST_PROPERTY);
        propertyService.save(ownerId, SECOND_PROPERTY);

        SECOND_PROPERTY.setCity("Kyiv");
        SECOND_PROPERTY.setAddress("K-street");
        SECOND_PROPERTY.setNumberOfRooms(4);

        propertyService.update(ownerId, SECOND_PROPERTY.getId(), SECOND_PROPERTY);

        List<Property> ownerProperties = propertyService.getAll(ownerId);
        ownerProperties.forEach(System.out::println);

        assertEquals(List.of(FIRST_PROPERTY, SECOND_PROPERTY), ownerProperties);
    }


    @Test
    void should_not_update_property_for_certain_owner_if_owner_id_is_wrong() {
        assertThrows(NoSuchOwnerException.class, () -> propertyService.update(INVALID_ID, SECOND_PROPERTY.getId(), SECOND_PROPERTY));
    }

    @Test
    void should_not_update_property_for_certain_owner_if_property_id_is_wrong() {
        ownerService.addNewOwner(OWNER);

        assertThrows(PropertyNotFoundException.class, () -> propertyService.update(OWNER.getId(), INVALID_ID, FIRST_PROPERTY));
    }

    @Test
    void should_not_update_property_for_certain_owner_if_property_is_null() {
        assertThrows(PropertyNotFoundException.class, () -> propertyService.update(1, SECOND_PROPERTY.getId(), null));
    }

    @Test
    void should_not_remove_property_for_certain_owner_if_property_id_is_null() {
        ownerService.addNewOwner(OWNER);

        assertThrows(PropertyNotFoundException.class, () -> propertyService.remove(OWNER.getId(), INVALID_ID));
    }

    @Test
    void should_remove_property_for_certain_owner() {
        ownerService.addNewOwner(OWNER);
        boolean isAdded = propertyService.save(OWNER.getId(), SECOND_PROPERTY);
        assertTrue(isAdded);

        boolean isRemoved = propertyService.remove(OWNER.getId(), SECOND_PROPERTY.getId());
        assertTrue(isRemoved);
    }

    @Test
    void should_not_remove_property_for_certain_owner_if_owner_id_is_wrong() {
        assertThrows(NoSuchOwnerException.class, () -> propertyService.remove(INVALID_ID, 1));
    }
}
