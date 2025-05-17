package com.tvo.propertyregister.integration;

import com.tvo.propertyregister.integration.config.repository.OwnerTestRepository;
import com.tvo.propertyregister.integration.config.repository.PropertyTestRepository;
import com.tvo.propertyregister.model.dto.BooleanResponseDto;
import com.tvo.propertyregister.model.dto.CreatePropertyDto;
import com.tvo.propertyregister.model.dto.ErrorDto;
import com.tvo.propertyregister.model.dto.UpdatePropertyDto;
import com.tvo.propertyregister.model.owner.FamilyStatus;
import com.tvo.propertyregister.model.owner.Owner;
import com.tvo.propertyregister.model.property.Property;
import com.tvo.propertyregister.model.property.PropertyCondition;
import com.tvo.propertyregister.model.property.PropertyType;
import com.tvo.propertyregister.service.OwnerService;
import com.tvo.propertyregister.service.PropertyService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.*;

public class PropertyServiceIntegrationTest extends AbstractServiceTest {

    @Autowired
    private TestRestTemplate restTemplate;

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
            new BigDecimal("0"), List.of());

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
        propertyService.add(OWNER.getId(), SECOND_PROPERTY);

        ResponseEntity<List<Property>> response = restTemplate.exchange(
                "/v1/owners/" + OWNER.getId() + "/properties",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        List<Property> properties = requireNonNull(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(SECOND_PROPERTY), properties);
    }

    @Test
    void should_get_all_properties_by_owner_id_if_the_id_is_wrong() {
        ResponseEntity<ErrorDto> response = restTemplate.exchange(
                "/v1/owners/" + OWNER.getId() + "/properties",
                HttpMethod.GET,
                null,
                ErrorDto.class
        );

        ErrorDto error = requireNonNull(response.getBody());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(error.detail().contains("was not found"));
    }

    @Test
    void should_get_all_properties_by_owner_id_if_owner_does_not_have_property() {
        Owner owner = new Owner(1, "Frank", "John",
                30, FamilyStatus.SINGLE,
                false, "frankjohn@gmail.com",
                "+456987123",
                LocalDate.of(1994, 5, 9),
                new BigDecimal("10000"), List.of());

        ownerService.addNewOwner(owner);

        ResponseEntity<List<Property>> response = restTemplate.exchange(
                "/v1/owners/" + owner.getId() + "/properties",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        List<Property> properties = requireNonNull(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(), properties);
    }

    @Test
    void should_add_new_property_to_certain_owner() {
        ownerService.addNewOwner(OWNER);

        CreatePropertyDto createPropertyDto = new CreatePropertyDto(
                FIRST_PROPERTY.getPropertyType(),
                FIRST_PROPERTY.getCity(),
                FIRST_PROPERTY.getAddress(),
                FIRST_PROPERTY.getSquare(),
                FIRST_PROPERTY.getNumberOfRooms(),
                FIRST_PROPERTY.getCost(),
                FIRST_PROPERTY.getDateOfBecomingOwner(),
                FIRST_PROPERTY.getDateOfBuilding(),
                FIRST_PROPERTY.getPropertyCondition()
        );

        HttpEntity<CreatePropertyDto> addingRequest = new HttpEntity<>(createPropertyDto);

        ResponseEntity<BooleanResponseDto> addingResponse = restTemplate.exchange(
                "/v1/owners/" + OWNER.getId() + "/properties",
                HttpMethod.POST,
                addingRequest,
                BooleanResponseDto.class
        );

        Owner actualOwner = ownerService.getOwnerById(OWNER.getId());

        assertEquals(HttpStatus.OK, addingResponse.getStatusCode());
        assertTrue(requireNonNull(addingResponse.getBody()).succeed());
        assertEquals(List.of(FIRST_PROPERTY), actualOwner.getProperties());
    }

    @Test
    void should_initialize_list_of_properties_if_it_is_null() {
        Owner owner = new Owner(1, "Frank", "John",
                30, FamilyStatus.SINGLE,
                false, "frankjohn@gmail.com",
                "+456987123",
                LocalDate.of(1994, 5, 9),
                new BigDecimal("10000"), null);

        ownerService.addNewOwner(owner);

        CreatePropertyDto createPropertyDto = new CreatePropertyDto(
                FIRST_PROPERTY.getPropertyType(),
                FIRST_PROPERTY.getCity(),
                FIRST_PROPERTY.getAddress(),
                FIRST_PROPERTY.getSquare(),
                FIRST_PROPERTY.getNumberOfRooms(),
                FIRST_PROPERTY.getCost(),
                FIRST_PROPERTY.getDateOfBecomingOwner(),
                FIRST_PROPERTY.getDateOfBuilding(),
                FIRST_PROPERTY.getPropertyCondition()
        );

        HttpEntity<CreatePropertyDto> addingRequest = new HttpEntity<>(createPropertyDto);

        ResponseEntity<BooleanResponseDto> addingResponse = restTemplate.exchange(
                "/v1/owners/" + owner.getId() + "/properties",
                HttpMethod.POST,
                addingRequest,
                BooleanResponseDto.class
        );

        Owner actualOwner = ownerService.getOwnerById(owner.getId());

        assertEquals(HttpStatus.OK, addingResponse.getStatusCode());
        assertTrue(requireNonNull(addingResponse.getBody()).succeed());
        assertNotNull(actualOwner.getProperties());
        assertEquals(List.of(FIRST_PROPERTY), actualOwner.getProperties());
    }

    @Test
    void should_not_initialize_list_of_properties_if_it_is_not_null() {
        List<Property> initialProperties = new ArrayList<>();
        Owner owner = new Owner(2, "Lisa", "Brown",
                28, FamilyStatus.MARRIED,
                false, "lisabrown@gmail.com",
                "+123456789",
                LocalDate.of(1995, 3, 14),
                new BigDecimal("15000"), initialProperties);

        ownerService.addNewOwner(owner);

        CreatePropertyDto createPropertyDto = new CreatePropertyDto(
                FIRST_PROPERTY.getPropertyType(),
                FIRST_PROPERTY.getCity(),
                FIRST_PROPERTY.getAddress(),
                FIRST_PROPERTY.getSquare(),
                FIRST_PROPERTY.getNumberOfRooms(),
                FIRST_PROPERTY.getCost(),
                FIRST_PROPERTY.getDateOfBecomingOwner(),
                FIRST_PROPERTY.getDateOfBuilding(),
                FIRST_PROPERTY.getPropertyCondition()
        );

        HttpEntity<CreatePropertyDto> addingRequest = new HttpEntity<>(createPropertyDto);

        ResponseEntity<BooleanResponseDto> addingResponse = restTemplate.exchange(
                "/v1/owners/" + owner.getId() + "/properties",
                HttpMethod.POST,
                addingRequest,
                BooleanResponseDto.class
        );

        Owner actualOwner = ownerService.getOwnerById(owner.getId());

        assertEquals(HttpStatus.OK, addingResponse.getStatusCode());
        assertTrue(requireNonNull(addingResponse.getBody()).succeed());
        assertEquals(1, actualOwner.getProperties().size());
        assertEquals(List.of(FIRST_PROPERTY), actualOwner.getProperties());
    }

    @Test
    void should_not_add_new_property_if_id_is_wrong() {
        CreatePropertyDto createPropertyDto = new CreatePropertyDto(
                PropertyType.HOUSE,
                "Prague",
                "Boris Niemcov Street 220",
                150,
                5,
                new BigDecimal("750000"),
                LocalDate.of(2020, 4, 10),
                LocalDate.of(2012, 1, 9),
                PropertyCondition.GOOD
        );

        HttpEntity<CreatePropertyDto> addingRequest = new HttpEntity<>(createPropertyDto);

        ResponseEntity<ErrorDto> addingResponse = restTemplate.exchange(
                "/v1/owners/" + INVALID_ID + "/properties",
                HttpMethod.POST,
                addingRequest,
                ErrorDto.class
        );

        ErrorDto error = requireNonNull(addingResponse.getBody());

        boolean errorIsValid = error.detail().contains("not found");

        assertEquals(HttpStatus.NOT_FOUND, addingResponse.getStatusCode());
        assertTrue(errorIsValid);
    }

    @Test
    void should_not_add_new_property_if_property_is_null() {
        ownerService.addNewOwner(OWNER);

        ResponseEntity<ErrorDto> addingResponse = restTemplate.exchange(
                "/v1/owners/" + OWNER.getId() + "/properties",
                HttpMethod.POST,
                null,
                ErrorDto.class
        );

        ErrorDto error = requireNonNull(addingResponse.getBody());

        boolean errorIsValid = error.detail().contains("Failed");

        assertEquals(HttpStatus.BAD_REQUEST, addingResponse.getStatusCode());
        assertTrue(errorIsValid);
    }

    @Test
    void should_update_property_for_certain_owner() {
        Owner owner = new Owner(2, "Linda", "Johnson",
                39, FamilyStatus.MARRIED,
                true, "lindajohnson@gmail.com",
                "+123456789",
                LocalDate.of(1986, 8, 9),
                new BigDecimal("0"), new ArrayList<>());

        Property property = new Property(2, PropertyType.HOUSE, "Prague", "Boris Niemcov Street 220",
                150, 5, new BigDecimal("750000"),
                LocalDate.of(2020, 4, 10),
                LocalDate.of(2012, 1, 9),
                PropertyCondition.GOOD);

        ownerService.addNewOwner(owner);

        int ownerId = owner.getId();

        propertyService.add(ownerId, FIRST_PROPERTY);
        propertyService.add(ownerId, property);

        property.setCity("Kyiv");
        property.setAddress("K-street");
        property.setNumberOfRooms(4);

        UpdatePropertyDto updatePropertyDto = new UpdatePropertyDto(
                property.getCity(),
                property.getAddress(),
                property.getNumberOfRooms(),
                property.getPropertyCondition()
        );

        HttpEntity<UpdatePropertyDto> updateRequest = new HttpEntity<>(updatePropertyDto);

        ResponseEntity<BooleanResponseDto> updateResponse = restTemplate.exchange(
                "/v1/owners/" + ownerId + "/properties/" + property.getId(),
                HttpMethod.PUT,
                updateRequest,
                BooleanResponseDto.class
        );

        Owner actualOwner = ownerService.getOwnerById(ownerId);
        BooleanResponseDto responseDto = requireNonNull(updateResponse.getBody());

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertTrue(responseDto.succeed());
        assertEquals(List.of(FIRST_PROPERTY, property), actualOwner.getProperties());
    }


    @Test
    void should_not_update_property_for_certain_owner_if_owner_id_is_wrong() {
        UpdatePropertyDto updatePropertyDto = new UpdatePropertyDto(
                SECOND_PROPERTY.getCity(),
                SECOND_PROPERTY.getAddress(),
                SECOND_PROPERTY.getNumberOfRooms(),
                SECOND_PROPERTY.getPropertyCondition()
        );

        HttpEntity<UpdatePropertyDto> request = new HttpEntity<>(updatePropertyDto);

        ResponseEntity<ErrorDto> response = restTemplate.exchange(
                "/v1/owners/" + INVALID_ID + "/properties/" + SECOND_PROPERTY.getId(),
                HttpMethod.PUT,
                request,
                ErrorDto.class
        );

        ErrorDto error = requireNonNull(response.getBody());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(error.detail().contains("not found"));
    }

    @Test
    void should_not_update_property_for_certain_owner_if_property_id_is_wrong() {
        ownerService.addNewOwner(OWNER);

        UpdatePropertyDto updatePropertyDto = new UpdatePropertyDto(
                FIRST_PROPERTY.getCity(),
                FIRST_PROPERTY.getAddress(),
                FIRST_PROPERTY.getNumberOfRooms(),
                FIRST_PROPERTY.getPropertyCondition()
        );

        HttpEntity<UpdatePropertyDto> request = new HttpEntity<>(updatePropertyDto);


        ResponseEntity<ErrorDto> response = restTemplate.exchange(
                "/v1/owners/" + OWNER.getId() + "/properties/" + INVALID_ID,
                HttpMethod.PUT,
                request,
                ErrorDto.class
        );

        ErrorDto error = requireNonNull(response.getBody());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(error.detail().contains("not found"));
    }

    @Test
    void should_not_update_property_for_certain_owner_if_property_is_null() {
        ownerService.addNewOwner(OWNER);

        ResponseEntity<ErrorDto> response = restTemplate.exchange(
                "/v1/owners/" + OWNER.getId() + "/properties/" + FIRST_PROPERTY.getId(),
                HttpMethod.PUT,
                null,
                ErrorDto.class
        );

        ErrorDto error = requireNonNull(response.getBody());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(error.detail().contains("Failed"));
    }

    @Test
    void should_not_remove_property_for_certain_owner_if_property_id_is_null() {
        ownerService.addNewOwner(OWNER);

        ResponseEntity<ErrorDto> response = restTemplate.exchange(
                "/v1/owners/" + OWNER.getId() + "/properties/" + INVALID_ID,
                HttpMethod.DELETE,
                null,
                ErrorDto.class
        );

        ErrorDto error = requireNonNull(response.getBody());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(error.detail().contains("not found"));
    }

    @Test
    void should_remove_property_for_certain_owner() {
        ownerService.addNewOwner(OWNER);
        propertyService.add(OWNER.getId(), SECOND_PROPERTY);

        ResponseEntity<BooleanResponseDto> response = restTemplate.exchange(
                "/v1/owners/" + OWNER.getId() + "/properties/" + SECOND_PROPERTY.getId(),
                HttpMethod.DELETE,
                null,
                BooleanResponseDto.class
        );

        BooleanResponseDto booleanResponseDto = requireNonNull(response.getBody());

        List<Property> properties = OWNER.getProperties();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(booleanResponseDto.succeed());
        assertEquals(List.of(), properties);
    }

    @Test
    void should_not_remove_property_for_certain_owner_if_owner_id_is_wrong() {
        ResponseEntity<ErrorDto> response = restTemplate.exchange(
                "/v1/owners/" + INVALID_ID + "/properties/" + FIRST_PROPERTY.getId(),
                HttpMethod.DELETE,
                null,
                ErrorDto.class
        );

        ErrorDto error = requireNonNull(response.getBody());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(error.detail().contains("not found"));
    }
}
