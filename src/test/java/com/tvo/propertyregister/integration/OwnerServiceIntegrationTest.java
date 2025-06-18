package com.tvo.propertyregister.integration;

import com.tvo.propertyregister.integration.config.repository.OwnerTestRepository;
import com.tvo.propertyregister.integration.config.repository.PropertyTestRepository;
import com.tvo.propertyregister.integration.config.repository.TaxRateTestRepository;
import com.tvo.propertyregister.model.TaxRate;
import com.tvo.propertyregister.model.dto.*;
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
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.*;


public class OwnerServiceIntegrationTest extends AbstractServiceTest {

    private static final int INVALID_ID = -1;

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

    private static final Owner MARRIED_OWNER_WITH_CHILDREN = new Owner(2, "Linda", "Johnson",
            39, FamilyStatus.MARRIED,
            true, "lindajohnson@gmail.com",
            "+123456789",
            LocalDate.of(1986, 8, 9),
            new BigDecimal("0"), List.of(FIRST_HOUSE));

    private static final Owner DEBTOR = new Owner(3, "Frank", "John",
            30, FamilyStatus.SINGLE,
            false, "frankjohn@gmail.com",
            "+456987123",
            LocalDate.of(1994, 5, 9),
            new BigDecimal("10000.0"), List.of(SECOND_HOUSE));

    @Autowired
    private OwnerService ownerService;

    @Autowired
    private OwnerTestRepository ownerTestRepository;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private TaxRateTestRepository taxRateTestRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.host", MONGO_DB_CONTAINER::getHost);
        registry.add("spring.data.mongodb.port", MONGO_DB_CONTAINER::getFirstMappedPort);

        registry.add("spring.cache.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.cache.redis.port", REDIS_CONTAINER::getFirstMappedPort);
    }

    @BeforeAll
    public static void startContainer() {
        MONGO_DB_CONTAINER.start();
        REDIS_CONTAINER.start();
    }

    @AfterAll
    public static void stopContainer() {
        REDIS_CONTAINER.stop();
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
        flushAllCache();
    }

    @Test
    void should_return_all_owners_when_the_list_is_empty() {
        ResponseEntity<List<Owner>> response = restTemplate.exchange(
                "/v1/owners",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        List<Owner> actualOwners = requireNonNull(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(), actualOwners);
    }

    @Test
    void should_return_all_owners_when_the_list_is_not_empty() {
        ownerService.addNewOwner(SINGLE_OWNER_WITHOUT_CHILDREN);

        ResponseEntity<List<Owner>> response = restTemplate.exchange(
                "/v1/owners",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        List<Owner> actualOwners = requireNonNull(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(SINGLE_OWNER_WITHOUT_CHILDREN), actualOwners);
    }

    @Test
    void should_return_owner_by_id() {
        ownerService.addNewOwner(SINGLE_OWNER_WITHOUT_CHILDREN);

        ResponseEntity<Owner> response = restTemplate.exchange(
                "/v1/owners/" + SINGLE_OWNER_WITHOUT_CHILDREN.getId(),
                HttpMethod.GET,
                null,
                Owner.class
        );

        Owner owner = requireNonNull(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(SINGLE_OWNER_WITHOUT_CHILDREN, owner);
    }

    @Test
    void should_return_owner_by_id_if_id_is_wrong() {
        ResponseEntity<ErrorDto> response = restTemplate.exchange(
                "/v1/owners/" + INVALID_ID,
                HttpMethod.GET,
                null,
                ErrorDto.class
        );

        ErrorDto errorDto = requireNonNull(response.getBody());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(errorDto.detail().contains("The owner with id"));
    }

    @Test
    void should_return_all_debtors_when_the_list_is_not_empty_but_no_debtors() {
        ownerService.addNewOwner(SINGLE_OWNER_WITHOUT_CHILDREN);

        ResponseEntity<List<Owner>> response = restTemplate.exchange(
                "/v1/owners/debtors",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        List<Owner> actualDebtors = requireNonNull(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(), actualDebtors);
    }

    @Test
    void should_return_all_debtors_when_the_list_is_empty() {
        ResponseEntity<List<Owner>> response = restTemplate.exchange(
                "/v1/owners/debtors",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        List<Owner> actualDebtors = requireNonNull(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(), actualDebtors);
    }

    @Test
    void should_return_all_debtors_when_the_list_is_not_empty_and_debtors_in_list() {
        ownerService.addNewOwner(SINGLE_OWNER_WITHOUT_CHILDREN);
        ownerService.addNewOwner(DEBTOR);

        ResponseEntity<List<Owner>> response = restTemplate.exchange(
                "/v1/owners/debtors",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        List<Owner> debtors = requireNonNull(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(DEBTOR), debtors);
    }

    @Test
    void should_return_all_debtors_when_there_is_only_debtors_in_list() {
        ownerService.addNewOwner(DEBTOR);

        ResponseEntity<List<Owner>> response = restTemplate.exchange(
                "/v1/owners/debtors",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        List<Owner> debtors = requireNonNull(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(DEBTOR), debtors);
    }

    @Test
    void should_save_new_owner() {
        CreateOwnerDto createOwnerDto = new CreateOwnerDto(
                SINGLE_OWNER_WITHOUT_CHILDREN.getFirstName(),
                SINGLE_OWNER_WITHOUT_CHILDREN.getLastName(),
                SINGLE_OWNER_WITHOUT_CHILDREN.getAge(),
                SINGLE_OWNER_WITHOUT_CHILDREN.getFamilyStatus(),
                SINGLE_OWNER_WITHOUT_CHILDREN.isHasChildren(),
                SINGLE_OWNER_WITHOUT_CHILDREN.getEmail(),
                SINGLE_OWNER_WITHOUT_CHILDREN.getPhoneNumber(),
                SINGLE_OWNER_WITHOUT_CHILDREN.getBirthday(),
                SINGLE_OWNER_WITHOUT_CHILDREN.getTaxesDebt()
        );

        HttpEntity<CreateOwnerDto> createDto = new HttpEntity<>(createOwnerDto);

        ResponseEntity<BooleanResponseDto> response = restTemplate.exchange(
                "/v1/owners",
                HttpMethod.POST,
                createDto,
                BooleanResponseDto.class
        );

        Owner owner = ownerService.getOwnerById(SINGLE_OWNER_WITHOUT_CHILDREN.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(requireNonNull(response.getBody()).succeed());
        assertTrue(compareOwners(SINGLE_OWNER_WITHOUT_CHILDREN, owner));
    }

    private boolean compareOwners(Owner expected, Owner actual) {
        return expected.getId() == actual.getId()
                && expected.getFirstName().equals(actual.getFirstName())
                && expected.getLastName().equals(actual.getLastName())
                && expected.getAge() == actual.getAge()
                && expected.getFamilyStatus().equals(actual.getFamilyStatus())
                && expected.isHasChildren() == actual.isHasChildren()
                && expected.getEmail().equals(actual.getEmail())
                && expected.getPhoneNumber().equals(actual.getPhoneNumber())
                && expected.getBirthday().equals(actual.getBirthday())
                && expected.getTaxesDebt().equals(actual.getTaxesDebt());
    }

    @Test
    void should_not_save_new_owner_if_owner_is_null() {
        ResponseEntity<ErrorDto> response = restTemplate.exchange(
                "/v1/owners",
                HttpMethod.POST,
                null,
                ErrorDto.class
        );

        ErrorDto error = requireNonNull(response.getBody());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(error.detail().contains("Failed to read request"));
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

        owner.setAge(31);
        owner.setHasChildren(true);

        UpdateOwnerDto updateOwnerDto = new UpdateOwnerDto(
                owner.getFirstName(),
                owner.getLastName(),
                owner.getAge(),
                owner.getFamilyStatus(),
                owner.isHasChildren(),
                owner.getEmail(),
                owner.getPhoneNumber(),
                owner.getBirthday(),
                owner.getTaxesDebt()
        );

        HttpEntity<UpdateOwnerDto> updateOwnerDtoHttpEntity = new HttpEntity<>(updateOwnerDto);

        ResponseEntity<BooleanResponseDto> response = restTemplate.exchange(
                "/v1/owners/" + owner.getId(),
                HttpMethod.PUT,
                updateOwnerDtoHttpEntity,
                BooleanResponseDto.class
        );

        ResponseEntity<Owner> getOwner = restTemplate.exchange(
                "/v1/owners/" + owner.getId(),
                HttpMethod.GET,
                null,
                Owner.class
        );

        Owner actualOwner = requireNonNull(getOwner.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(requireNonNull(response.getBody()).succeed());
        assertEquals(HttpStatus.OK, getOwner.getStatusCode());
        assertTrue(compareOwners(owner, actualOwner));
    }

    @Test
    void should_not_update_owner_and_throw_exception_if_does_not_exist() {
        HttpEntity<UpdateOwnerDto> request = new HttpEntity<>(null);

        ResponseEntity<ErrorDto> response = restTemplate.exchange(
                "/v1/owners/" + SINGLE_OWNER_WITHOUT_CHILDREN.getId(),
                HttpMethod.PUT,
                request,
                ErrorDto.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void should_not_update_owner_and_throw_exception_if_not_found_by_id() {
        HttpEntity<UpdateOwnerDto> request = new HttpEntity<>(new UpdateOwnerDto(
                SINGLE_OWNER_WITHOUT_CHILDREN.getFirstName(),
                SINGLE_OWNER_WITHOUT_CHILDREN.getLastName(),
                SINGLE_OWNER_WITHOUT_CHILDREN.getAge(),
                SINGLE_OWNER_WITHOUT_CHILDREN.getFamilyStatus(),
                SINGLE_OWNER_WITHOUT_CHILDREN.isHasChildren(),
                SINGLE_OWNER_WITHOUT_CHILDREN.getEmail(),
                SINGLE_OWNER_WITHOUT_CHILDREN.getPhoneNumber(),
                SINGLE_OWNER_WITHOUT_CHILDREN.getBirthday(),
                SINGLE_OWNER_WITHOUT_CHILDREN.getTaxesDebt()
        ));

        ResponseEntity<ErrorDto> response = restTemplate.exchange(
                "/v1/owners/" + INVALID_ID,
                HttpMethod.PUT,
                request,
                ErrorDto.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().detail().contains("does not exists"));
    }

    @Test
    void should_remove_owner() {
        ownerService.addNewOwner(SINGLE_OWNER_WITHOUT_CHILDREN);

        ResponseEntity<BooleanResponseDto> deleteResponse = restTemplate.exchange(
                "/v1/owners/" + SINGLE_OWNER_WITHOUT_CHILDREN.getId(),
                HttpMethod.DELETE,
                null,
                BooleanResponseDto.class
        );

        ResponseEntity<ErrorDto> getResponse = restTemplate.exchange(
                "/v1/owners" + SINGLE_OWNER_WITHOUT_CHILDREN.getId(),
                HttpMethod.GET,
                null,
                ErrorDto.class
        );

        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        assertTrue(requireNonNull(deleteResponse.getBody()).succeed());
        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    void should_return_false_if_nothing_to_remove() {
        ResponseEntity<ErrorDto> deleteResponse = restTemplate.exchange(
                "/v1/owners" + INVALID_ID,
                HttpMethod.DELETE,
                null,
                ErrorDto.class
        );

        assertEquals(HttpStatus.NOT_FOUND, deleteResponse.getStatusCode());
    }

    @Test
    void should_count_tax_obligations_no_leeway() {
        ownerService.addNewOwner(SINGLE_OWNER_WITHOUT_CHILDREN);
        propertyService.add(SINGLE_OWNER_WITHOUT_CHILDREN.getId(), FLAT);

        ResponseEntity<TaxObligationResponseDto> countTaxResponse = restTemplate.exchange(
                "/v1/owners/" + SINGLE_OWNER_WITHOUT_CHILDREN.getId() + "/tax-obligations",
                HttpMethod.GET,
                null,
                TaxObligationResponseDto.class
        );

        BigDecimal expectedTaxObligation = new BigDecimal("420");
        TaxObligationResponseDto taxObligationResponseDto = requireNonNull(countTaxResponse.getBody());
        BigDecimal actualTaxObligation = taxObligationResponseDto.taxObligation();

        assertEquals(HttpStatus.OK, countTaxResponse.getStatusCode());
        assertEquals(expectedTaxObligation, actualTaxObligation);
    }

    @Test
    void should_count_tax_obligations_with_multiple_leeway() {
        ownerService.addNewOwner(MARRIED_OWNER_WITH_CHILDREN);
        propertyService.add(MARRIED_OWNER_WITH_CHILDREN.getId(), FIRST_HOUSE);

        ResponseEntity<TaxObligationResponseDto> countTaxResponse = restTemplate.exchange(
                "/v1/owners/" + MARRIED_OWNER_WITH_CHILDREN.getId() + "/tax-obligations",
                HttpMethod.GET,
                null,
                TaxObligationResponseDto.class
        );

        BigDecimal expectedTaxObligation = new BigDecimal("960.0");
        TaxObligationResponseDto taxObligationResponseDto = requireNonNull(countTaxResponse.getBody());
        BigDecimal actualTaxObligation = taxObligationResponseDto.taxObligation();

        assertEquals(HttpStatus.OK, countTaxResponse.getStatusCode());
        assertEquals(expectedTaxObligation, actualTaxObligation);
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
        propertyService.add(id, FLAT);

        ResponseEntity<TaxObligationResponseDto> countTaxResponse = restTemplate.exchange(
                "/v1/owners/" + owner.getId() + "/tax-obligations",
                HttpMethod.GET,
                null,
                TaxObligationResponseDto.class
        );

        BigDecimal expectedTaxObligation = new BigDecimal("378.0");
        TaxObligationResponseDto taxObligationResponseDto = requireNonNull(countTaxResponse.getBody());
        BigDecimal actualTaxObligation = taxObligationResponseDto.taxObligation();

        assertEquals(HttpStatus.OK, countTaxResponse.getStatusCode());
        assertEquals(expectedTaxObligation, actualTaxObligation);
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
        propertyService.add(id, FLAT);

        ResponseEntity<TaxObligationResponseDto> countTaxResponse = restTemplate.exchange(
                "/v1/owners/" + owner.getId() + "/tax-obligations",
                HttpMethod.GET,
                null,
                TaxObligationResponseDto.class
        );

        BigDecimal expectedTaxObligation = new BigDecimal("294.0");
        TaxObligationResponseDto taxObligationResponseDto = requireNonNull(countTaxResponse.getBody());
        BigDecimal actualTaxObligation = taxObligationResponseDto.taxObligation();

        assertEquals(expectedTaxObligation, actualTaxObligation);
    }

    @Test
    void should_not_count_tax_obligations_if_owner_is_null() {
        ResponseEntity<ErrorDto> countTaxResponse = restTemplate.exchange(
                "/v1/owners/" + INVALID_ID + "/tax-obligations",
                HttpMethod.GET,
                null,
                ErrorDto.class
        );

        ErrorDto error = requireNonNull(countTaxResponse.getBody());

        assertEquals(HttpStatus.NOT_FOUND, countTaxResponse.getStatusCode());
        assertTrue(error.detail().contains("does not exists"));
    }

    @Test
    void should_throw_property_not_found_exception_if_property_is_null() {
        int id = 1;

        Owner owner = new Owner(id, "John", "Smith",
                30, FamilyStatus.SINGLE,
                true, "johnsmith@gmail.com",
                "+456987123",
                LocalDate.of(1994, 8, 9),
                new BigDecimal("0"), null);
        ownerService.addNewOwner(owner);

        ResponseEntity<ErrorDto> countTaxResponse = restTemplate.exchange(
                "/v1/owners/" + owner.getId() + "/tax-obligations",
                HttpMethod.GET,
                null,
                ErrorDto.class
        );

        ErrorDto error = requireNonNull(countTaxResponse.getBody());
        assertEquals(HttpStatus.NOT_FOUND, countTaxResponse.getStatusCode());
        assertTrue(error.detail().contains("The list of property does not exist"));
    }

    @Test
    void should_throw_exception_if_tax_rate_number_is_invalid() {
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

        ResponseEntity<ErrorDto> countTaxResponse = restTemplate.exchange(
                "/v1/owners/" + owner.getId() + "/tax-obligations",
                HttpMethod.GET,
                null,
                ErrorDto.class
        );

        ErrorDto error = requireNonNull(countTaxResponse.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, countTaxResponse.getStatusCode());
        assertTrue(error.detail().contains("Invalid number of tax rates"));
    }

    @Test
    void should_count_debt_for_all_owners() {
        Owner firstOwner = new Owner(1, "John", "Smith",
                30, FamilyStatus.SINGLE,
                true, "johnsmith@gmail.com",
                "+456987123",
                LocalDate.of(1994, 8, 9),
                new BigDecimal("15000.0"), List.of(FLAT));
        Owner secondOwner = new Owner(2, "Linda", "Johnson",
                39, FamilyStatus.MARRIED,
                true, "lindajohnson@gmail.com",
                "+123456789",
                LocalDate.of(1986, 8, 9),
                new BigDecimal("0"), List.of(FIRST_HOUSE));
        Owner thirdOwner = new Owner(3, "Frank", "John",
                30, FamilyStatus.SINGLE,
                false, "frankjohn@gmail.com",
                "+456987123",
                LocalDate.of(1994, 5, 9),
                new BigDecimal("10000.0"), List.of(SECOND_HOUSE));

        ownerService.addNewOwner(firstOwner);
        ownerService.addNewOwner(secondOwner);
        ownerService.addNewOwner(thirdOwner);

        ResponseEntity<BigDecimal> response = restTemplate.exchange(
                "/v1/owners/totalDebt",
                HttpMethod.GET,
                null,
                BigDecimal.class
        );

        BigDecimal actualTotalDebt = requireNonNull(response.getBody());
        BigDecimal expectedTotalDebt = firstOwner.getTaxesDebt()
                .add(secondOwner.getTaxesDebt())
                .add(thirdOwner.getTaxesDebt());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedTotalDebt, actualTotalDebt);
    }

    @Test
    void should_return_zero_if_there_is_no_debtors() {
        Owner firstOwner = new Owner(1, "John", "Smith",
                30, FamilyStatus.SINGLE,
                true, "johnsmith@gmail.com",
                "+456987123",
                LocalDate.of(1994, 8, 9),
                new BigDecimal("0"), List.of(FLAT));
        Owner secondOwner = new Owner(2, "Linda", "Johnson",
                39, FamilyStatus.MARRIED,
                true, "lindajohnson@gmail.com",
                "+123456789",
                LocalDate.of(1986, 8, 9),
                new BigDecimal("0"), List.of(FIRST_HOUSE));

        ownerService.addNewOwner(firstOwner);
        ownerService.addNewOwner(secondOwner);

        ResponseEntity<BigDecimal> response = restTemplate.exchange(
                "/v1/owners/totalDebt",
                HttpMethod.GET,
                null,
                BigDecimal.class
        );

        BigDecimal actualTotalDebt = requireNonNull(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(BigDecimal.ZERO, actualTotalDebt);
    }

    @Test
    void should_count_debt_for_all_owners_when_there_is_only_one_debtor() {
        Owner firstOwner = new Owner(1, "John", "Smith",
                30, FamilyStatus.SINGLE,
                true, "johnsmith@gmail.com",
                "+456987123",
                LocalDate.of(1994, 8, 9),
                new BigDecimal("0"), List.of(FLAT));
        Owner secondOwner = new Owner(2, "Linda", "Johnson",
                39, FamilyStatus.MARRIED,
                true, "lindajohnson@gmail.com",
                "+123456789",
                LocalDate.of(1986, 8, 9),
                new BigDecimal("0"), List.of(FIRST_HOUSE));
        Owner thirdOwner = new Owner(3, "Frank", "John",
                30, FamilyStatus.SINGLE,
                false, "frankjohn@gmail.com",
                "+456987123",
                LocalDate.of(1994, 5, 9),
                new BigDecimal("10000.0"), List.of(SECOND_HOUSE));

        ownerService.addNewOwner(firstOwner);
        ownerService.addNewOwner(secondOwner);
        ownerService.addNewOwner(thirdOwner);

        ResponseEntity<BigDecimal> response = restTemplate.exchange(
                "/v1/owners/totalDebt",
                HttpMethod.GET,
                null,
                BigDecimal.class
        );

        BigDecimal actualTotalDebt = requireNonNull(response.getBody());
        BigDecimal expectedTotalDebt = firstOwner.getTaxesDebt()
                .add(secondOwner.getTaxesDebt())
                .add(thirdOwner.getTaxesDebt());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedTotalDebt, actualTotalDebt);
    }
}
