package com.tvo.propertyregister.integration;

import com.tvo.propertyregister.exception.NoSuchOwnerException;
import com.tvo.propertyregister.exception.PropertyNotFoundException;
import com.tvo.propertyregister.integration.config.TestConfig;
import com.tvo.propertyregister.model.owner.FamilyStatus;
import com.tvo.propertyregister.model.owner.Owner;
import com.tvo.propertyregister.model.property.Property;
import com.tvo.propertyregister.model.property.PropertyCondition;
import com.tvo.propertyregister.model.property.PropertyType;
import com.tvo.propertyregister.repository.OwnerRepository;
import com.tvo.propertyregister.repository.PropertyRepository;
import com.tvo.propertyregister.service.OwnerService;
import com.tvo.propertyregister.service.PropertyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.annotation.JsonAppend;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
public class PropertyServiceIntegrationTests {

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private OwnerService ownerService;

    @Autowired
    private OwnerRepository ownerRepository;

    private final Property PROPERTY_1 = new Property(
            1, PropertyType.FLAT, "Prague", "Heroev Street 24",
            70, 3, new BigDecimal("500000"),
            LocalDate.of(2020, 4, 10),
            LocalDate.of(2012, 1, 9),
            PropertyCondition.GOOD);

    @BeforeEach
    void cleanUp() {
        propertyRepository.clear();
        ownerRepository.clear();
    }

    private Property createProperty() {
        return new Property(2, PropertyType.HOUSE, "Prague", "Boris Niemcov Street 220",
                150, 5, new BigDecimal("750000"),
                LocalDate.of(2020, 4, 10),
                LocalDate.of(2012, 1, 9),
                PropertyCondition.GOOD);
    }

    @Test
    void should_get_all_properties_by_owner_id() {
        int ownerId = 1;
        Property property = createProperty();

        propertyService.addNewProperty(ownerId, property);

        List<Property> actualProperties = propertyService.getAll(ownerId);

        assertEquals(List.of(PROPERTY_1, property), actualProperties);
    }

    @Test
    void should_get_all_properties_by_owner_id_if_the_id_is_wrong() {
        assertThrows(NoSuchOwnerException.class, () -> propertyService.getAll(100));
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
        int ownerId = 1;
        Property property = createProperty();

        boolean result = propertyService.addNewProperty(ownerId, property);
        assertTrue(result);
    }

    @Test
    void should_update_property_for_certain_owner() {

    }

    @Test
    void should_remove_property_for_certain_owner() {

    }
}
