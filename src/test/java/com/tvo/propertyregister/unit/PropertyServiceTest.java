package com.tvo.propertyregister.unit;

import com.tvo.propertyregister.exception.NoSuchOwnerException;
import com.tvo.propertyregister.exception.PropertyNotFoundException;
import com.tvo.propertyregister.model.owner.FamilyStatus;
import com.tvo.propertyregister.model.owner.Owner;
import com.tvo.propertyregister.model.property.Property;
import com.tvo.propertyregister.model.property.PropertyCondition;
import com.tvo.propertyregister.model.property.PropertyType;
import com.tvo.propertyregister.repository.PropertyRepository;
import com.tvo.propertyregister.service.PropertyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PropertyServiceTest {

    @Mock
    private PropertyRepository propertyRepository;

    @InjectMocks
    private PropertyService propertyService;


    private static final Property FIRST_PROPERTY = new Property(
            1,
            PropertyType.FLAT,
            "Prague",
            "Glorian street 96",
            90,
            4,
            new BigDecimal("550000"),
            LocalDate.of(2022, 8, 9),
            LocalDate.of(2019, 12, 28),
            PropertyCondition.GOOD);

    private static final Property SECOND_PROPERTY = new Property(
            2,
            PropertyType.HOUSE,
            "Prague",
            "Glorian street 96",
            140,
            6,
            new BigDecimal("750000"),
            LocalDate.of(2018, 5, 17),
            LocalDate.of(2017, 6, 28),
            PropertyCondition.GOOD);

    private static final Property THIRD_PROPERTY = new Property(
            1,
            PropertyType.FLAT,
            "Prague",
            "Ukrainian street 14",
            100,
            5,
            new BigDecimal("750000"),
            LocalDate.of(2024, 8, 9),
            LocalDate.of(2022, 12, 28),
            PropertyCondition.GOOD);

    private static final Owner OWNER = new Owner(
            1,
            "Nick",
            "Ray",
            45,
            FamilyStatus.SINGLE,
            false,
            "nickray@gmail.com",
            "+420556897102",
            LocalDate.of(1980, 4, 8),
            new BigDecimal("0"),
            List.of(FIRST_PROPERTY)
    );

    @Test
    public void should_return_all_properties_by_owner_id() {
        when(propertyService.getAll(OWNER.getId())).thenReturn(OWNER.getProperties());

        List<Property> factualProperties = propertyService.getAll(OWNER.getId());

        assertEquals(OWNER.getProperties(), factualProperties);
    }

    @Test
    public void should_not_return_properties_if_property_list_is_empty() {
        when(propertyService.getAll(OWNER.getId())).thenReturn(List.of());

        List<Property> properties = propertyService.getAll(OWNER.getId());

        assertEquals(List.of(), properties);
    }

    @Test
    public void should_add_new_property_to_certain_owner() {
        propertyService.addNewProperty(OWNER.getId(), SECOND_PROPERTY);

        verify(propertyRepository, times(1)).save(OWNER.getId(), SECOND_PROPERTY);
    }

    @Test
    public void should_not_add_new_property_if_owner_does_not_exists() {
        int invalidId = -1;

        when(propertyService.addNewProperty(invalidId, SECOND_PROPERTY)).thenThrow(NoSuchOwnerException.class);

        assertThrows(NoSuchOwnerException.class, () -> propertyService.addNewProperty(invalidId, SECOND_PROPERTY));
    }

    @Test
    public void should_update_property_info() {
        propertyService.updatePropertyInfo(OWNER.getId(), FIRST_PROPERTY.getId(), THIRD_PROPERTY);

        verify(propertyRepository, times(1)).update(OWNER.getId(), FIRST_PROPERTY.getId(), THIRD_PROPERTY);
    }

    @Test
    public void should_not_update_property_info_if_owner_does_not_exists() {
        int invalidOwnerId = -1;

        when(propertyService.updatePropertyInfo(invalidOwnerId, FIRST_PROPERTY.getId(), THIRD_PROPERTY)).thenThrow(NoSuchOwnerException.class);

        assertThrows(NoSuchOwnerException.class, () -> propertyService.updatePropertyInfo(invalidOwnerId, FIRST_PROPERTY.getId(), THIRD_PROPERTY));
    }

    @Test
    public void should_not_update_property_info_if_property_does_not_exists() {
        int invalidPropertyId = -1;

        when(propertyService.updatePropertyInfo(OWNER.getId(), invalidPropertyId, THIRD_PROPERTY)).thenThrow(PropertyNotFoundException.class);

        assertThrows(PropertyNotFoundException.class, () -> propertyService.updatePropertyInfo(OWNER.getId(), invalidPropertyId, THIRD_PROPERTY));
    }

    @Test
    public void should_delete_property() {
        propertyService.remove(OWNER.getId(), FIRST_PROPERTY.getId());

        verify(propertyRepository, times(1)).remove(OWNER.getId(), FIRST_PROPERTY.getId());
    }

    @Test
    public void should_not_delete_property_if_owner_does_not_exists() {
        int invalidOwnerId = -1;

        when(propertyService.remove(invalidOwnerId, FIRST_PROPERTY.getId())).thenThrow(NoSuchOwnerException.class);

        assertThrows(NoSuchOwnerException.class, () -> propertyService.remove(invalidOwnerId, FIRST_PROPERTY.getId()));
    }
}
