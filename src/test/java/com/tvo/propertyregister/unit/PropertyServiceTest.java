package com.tvo.propertyregister.unit;

import com.tvo.propertyregister.exception.NoSuchOwnerException;
import com.tvo.propertyregister.exception.PropertyNotFoundException;
import com.tvo.propertyregister.model.owner.FamilyStatus;
import com.tvo.propertyregister.model.owner.Owner;
import com.tvo.propertyregister.model.property.Property;
import com.tvo.propertyregister.model.property.PropertyCondition;
import com.tvo.propertyregister.model.property.PropertyType;
import com.tvo.propertyregister.repository.OwnerRepository;
import com.tvo.propertyregister.repository.PropertyRepository;
import com.tvo.propertyregister.service.PropertyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PropertyServiceTest {

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private OwnerRepository ownerRepository;

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

    private static final Owner INVALID_OWNER = new Owner(
            -1,
            "text@gmail.com",
            "Helen",
            "Rocks",
            new BigDecimal("0"),
            false,
            FamilyStatus.SINGLE);

    private static final Property INVALID_PROPERTY = new Property(
            -1,
            PropertyType.FLAT,
            "Kelin",
            "Yangston street 14",
            100,
            5,
            new BigDecimal("750000"),
            LocalDate.of(2024, 8, 9),
            LocalDate.of(2022, 12, 28),
            PropertyCondition.GOOD);

    @Test
    public void should_return_all_properties_by_owner_id() {
        when(ownerRepository.findById(OWNER.getId())).thenReturn(OWNER);
        when(propertyRepository.findAll(OWNER.getId())).thenReturn(OWNER.getProperties());

        List<Property> factualProperties = propertyService.getAll(OWNER.getId());

        assertEquals(OWNER.getProperties(), factualProperties);

        verify(propertyRepository, times(1)).findAll(OWNER.getId());
    }

    @Test
    public void should_not_return_properties_if_property_list_is_empty() {
        when(ownerRepository.findById(OWNER.getId())).thenReturn(OWNER);
        when(propertyRepository.findAll(OWNER.getId())).thenReturn(List.of());

        List<Property> properties = propertyService.getAll(OWNER.getId());

        assertEquals(List.of(), properties);

        verify(propertyRepository, times(1)).findAll(OWNER.getId());
    }

    @Test
    public void should_not_return_properties_if_owner_does_not_exists() {
        when(ownerRepository.findById(OWNER.getId())).thenThrow(NoSuchOwnerException.class);

        assertThrows(NoSuchOwnerException.class, () -> propertyService.getAll(OWNER.getId()));
    }

    @Test
    public void should_add_new_property_to_certain_owner() {
        when(ownerRepository.findById(OWNER.getId())).thenReturn(OWNER);
        propertyService.add(OWNER.getId(), SECOND_PROPERTY);

        verify(propertyRepository, times(1)).save(OWNER, SECOND_PROPERTY);
    }

    @Test
    public void should_not_add_new_property_if_owner_does_not_exists() {
        when(ownerRepository.findById(INVALID_OWNER.getId())).thenReturn(null);

        assertThrows(NoSuchOwnerException.class, () -> propertyService.add(INVALID_OWNER.getId(), SECOND_PROPERTY));
    }

    @Test
    public void should_update_property_info() {
        when(ownerRepository.findById(OWNER.getId())).thenReturn(OWNER);
        propertyService.update(OWNER.getId(), FIRST_PROPERTY.getId(), THIRD_PROPERTY);

        Property propertyToUpdate = OWNER.getProperties().stream()
                .filter(property -> property.getId() == FIRST_PROPERTY.getId())
                .findFirst()
                .orElseThrow(() -> new PropertyNotFoundException("Property not found with ID: " + FIRST_PROPERTY.getId()));

        propertyToUpdate.setCity(THIRD_PROPERTY.getCity());
        propertyToUpdate.setAddress(THIRD_PROPERTY.getAddress());
        propertyToUpdate.setNumberOfRooms(THIRD_PROPERTY.getNumberOfRooms());
        propertyToUpdate.setPropertyCondition(THIRD_PROPERTY.getPropertyCondition());

        List<Property> properties = new ArrayList<>(OWNER.getProperties().stream()
                .filter(property -> property.getId() != FIRST_PROPERTY.getId())
                .toList());

        properties.add(propertyToUpdate);

        verify(propertyRepository, times(1)).update(OWNER.getId(), properties);
    }

    @Test
    public void should_not_update_property_info_if_owner_does_not_exists() {
        when(ownerRepository.findById(INVALID_OWNER.getId())).thenThrow(NoSuchOwnerException.class);

        assertThrows(NoSuchOwnerException.class, () -> propertyService.update(INVALID_OWNER.getId(), FIRST_PROPERTY.getId(), THIRD_PROPERTY));
    }

    @Test
    public void should_not_update_property_info_if_property_does_not_exists() {
        when(ownerRepository.findById(OWNER.getId())).thenReturn(OWNER);

        assertThrows(PropertyNotFoundException.class, () -> propertyService.update(OWNER.getId(), INVALID_OWNER.getId(), THIRD_PROPERTY));
    }

    @Test
    public void should_delete_property() {
        when(ownerRepository.findById(OWNER.getId())).thenReturn(OWNER);
        propertyService.remove(OWNER.getId(), FIRST_PROPERTY.getId());

        List<Property> properties = new ArrayList<>(OWNER.getProperties().stream()
                .filter(property -> property.getId() != FIRST_PROPERTY.getId())
                .toList());

        verify(propertyRepository, times(1)).update(OWNER.getId(), properties);
    }

    @Test
    public void should_not_delete_property_if_owner_does_not_exists() {
        when(ownerRepository.findById(INVALID_OWNER.getId())).thenThrow(NoSuchOwnerException.class);

        assertThrows(NoSuchOwnerException.class, () -> propertyService.remove(INVALID_OWNER.getId(), FIRST_PROPERTY.getId()));
    }
}
