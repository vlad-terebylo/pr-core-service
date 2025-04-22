package com.tvo.propertyregister.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvo.propertyregister.exception.DontHaveTaxDebtsException;
import com.tvo.propertyregister.exception.NoDebtorsInDebtorListException;
import com.tvo.propertyregister.exception.NoSuchOwnerException;
import com.tvo.propertyregister.integration.config.repository.OwnerTestRepository;
import com.tvo.propertyregister.model.dto.EmailEventDto;
import com.tvo.propertyregister.model.dto.EmailType;
import com.tvo.propertyregister.model.owner.FamilyStatus;
import com.tvo.propertyregister.model.owner.Owner;
import com.tvo.propertyregister.model.property.Property;
import com.tvo.propertyregister.model.property.PropertyCondition;
import com.tvo.propertyregister.model.property.PropertyType;
import com.tvo.propertyregister.service.DebtorNotificationService;
import com.tvo.propertyregister.service.EmailSender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.containers.RabbitMQContainer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.tvo.propertyregister.service.utils.Constants.EMAIL_TOPIC;
import static org.junit.jupiter.api.Assertions.*;

public class DebtorNotificationServiceIntegrationTests extends AbstractServiceTest {

    @Autowired
    private OwnerTestRepository ownerTestRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private DebtorNotificationService debtorNotificationService;

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private ObjectMapper mapper;

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

    private static final RabbitMQContainer RABBIT_MQ_CONTAINER =
            new RabbitMQContainer("rabbitmq:3.9-management");

    @BeforeAll
    static void setUp() {
        RABBIT_MQ_CONTAINER.start();
        System.setProperty("spring.rabbitmq.host", RABBIT_MQ_CONTAINER.getHost());
        System.setProperty("spring.rabbitmq.port", RABBIT_MQ_CONTAINER.getAmqpPort().toString());
    }

    @BeforeEach
    void cleanUp() {
        ownerTestRepository.clear();
    }

    @Test
    void should_send_notification_to_all_debtors() throws JsonProcessingException {
        Owner debtor = new Owner(1, "Frank", "John",
                30, FamilyStatus.SINGLE,
                false, "frankjohn@gmail.com",
                "+456987123",
                LocalDate.of(1994, 5, 9),
                new BigDecimal("10000.0"), List.of(HOUSE_1));

        EmailEventDto expectedEmailDto = new EmailEventDto(
                debtor.getEmail(),
                EmailType.ALL_DEBTOR_NOTIFICATION,
                Map.of("firstName", debtor.getFirstName(),
                        "lastName", debtor.getLastName(),
                        "debt", String.valueOf(debtor.getTaxesDebt()),
                        "numberOfDebtors", String.valueOf(1)
                )
        );

        ownerTestRepository.save(debtor);

        debtorNotificationService.notifyAllDebtors();

        String body = (String) rabbitTemplate.receiveAndConvert(EMAIL_TOPIC);

        assertTrue(Objects.nonNull(body));

        EmailEventDto receivedEmailDto = mapper.readValue(body, EmailEventDto.class);

        assertEquals(expectedEmailDto, receivedEmailDto);
    }

    @Test
    void should_send_notification_if_there_is_more_than_one_debtor() throws JsonProcessingException {
        Owner debtor1 = new Owner(1, "Frank", "John",
                30, FamilyStatus.SINGLE,
                false, "frankjohn@gmail.com",
                "+456987123",
                LocalDate.of(1994, 5, 9),
                new BigDecimal("10000.0"), List.of(HOUSE_1));

        Owner debtor2 = new Owner(2, "Alice", "Wonder",
                28, FamilyStatus.SINGLE,
                false, "alicewonder@gmail.com",
                "+111111111", LocalDate.of(1997, 1, 1),
                new BigDecimal("20000.0"), List.of(HOUSE_2));

        EmailEventDto expectedEmailDto1 = new EmailEventDto(
                debtor1.getEmail(),
                EmailType.ALL_DEBTOR_NOTIFICATION,
                Map.of("firstName", debtor1.getFirstName(),
                        "lastName", debtor1.getLastName(),
                        "debt", String.valueOf(debtor1.getTaxesDebt()),
                        "numberOfDebtors", String.valueOf(2)
                )
        );

        EmailEventDto expectedEmailDto2 = new EmailEventDto(
                debtor2.getEmail(),
                EmailType.ALL_DEBTOR_NOTIFICATION,
                Map.of("firstName", debtor2.getFirstName(),
                        "lastName", debtor2.getLastName(),
                        "debt", String.valueOf(debtor2.getTaxesDebt()),
                        "numberOfDebtors", String.valueOf(2)
                )
        );

        ownerTestRepository.save(debtor1);
        ownerTestRepository.save(debtor2);

        debtorNotificationService.notifyAllDebtors();

        String body = (String) rabbitTemplate.receiveAndConvert(EMAIL_TOPIC);
        String body2 = (String) rabbitTemplate.receiveAndConvert(EMAIL_TOPIC);

        assertTrue(Objects.nonNull(body));
        assertTrue(Objects.nonNull(body2));

        EmailEventDto receivedEmail1 = mapper.readValue(body, EmailEventDto.class);
        EmailEventDto receivedEmail2 = mapper.readValue(body2, EmailEventDto.class);

        assertEquals(expectedEmailDto1, receivedEmail1);
        assertEquals(expectedEmailDto2, receivedEmail2);
    }

    @Test
    void should_send_notification_if_there_is_debtor_and_owner_without_debts() throws JsonProcessingException {
        Owner debtor = new Owner(1, "Frank", "John",
                30, FamilyStatus.SINGLE,
                false, "frankjohn@gmail.com",
                "+456987123",
                LocalDate.of(1994, 5, 9),
                new BigDecimal("10000.0"), List.of(HOUSE_1));

        Owner owner = new Owner(2, "Alice", "Wonder",
                28, FamilyStatus.SINGLE,
                false, "alicewonder@gmail.com",
                "+111111111", LocalDate.of(1997, 1, 1),
                new BigDecimal("0"), List.of(HOUSE_2));

        EmailEventDto expectedEvent = new EmailEventDto(
                debtor.getEmail(),
                EmailType.ALL_DEBTOR_NOTIFICATION,
                Map.of("firstName", debtor.getFirstName(),
                        "lastName", debtor.getLastName(),
                        "debt", String.valueOf(debtor.getTaxesDebt()),
                        "numberOfDebtors", String.valueOf(1)
                )
        );

        ownerTestRepository.save(debtor);
        ownerTestRepository.save(owner);

        debtorNotificationService.notifyAllDebtors();

        String body = (String) rabbitTemplate.receiveAndConvert(EMAIL_TOPIC);
        String body2 = (String) rabbitTemplate.receiveAndConvert(EMAIL_TOPIC);

        assertTrue(Objects.nonNull(body));
        assertNull(body2);

        EmailEventDto receivedEvent = mapper.readValue(body, EmailEventDto.class);

        assertEquals(expectedEvent, receivedEvent);
    }

    @Test
    void should_not_notify_debtors_if_the_list_is_empty() {
        assertThrows(NoDebtorsInDebtorListException.class, () -> debtorNotificationService.notifyAllDebtors());
    }

    @Test
    void should_send_email_event_to_rabbitmq_queue() throws JsonProcessingException {
        EmailEventDto expectedEvent = new EmailEventDto(
                "terebylov@ssemi.cz",
                EmailType.ALL_DEBTOR_NOTIFICATION,
                Map.of("firstName", "John",
                        "lastName", "Doe",
                        "debt", "10000",
                        "numberOfDebtors", String.valueOf(1))
        );

        emailSender.send(expectedEvent);

        String body = (String) rabbitTemplate.receiveAndConvert(EMAIL_TOPIC);

        assertTrue(Objects.nonNull(body));

        EmailEventDto receivedEvent = mapper.readValue(body, EmailEventDto.class);

        assertTrue(Objects.nonNull(receivedEvent));
        assertEquals(expectedEvent, receivedEvent);
    }

    @Test
    void should_send_notification_to_certain_debtor_by_id() throws JsonProcessingException {
        Owner debtor = new Owner(1, "Frank", "John",
                30, FamilyStatus.SINGLE,
                false, "frankjohn@gmail.com",
                "+456987123",
                LocalDate.of(1994, 5, 9),
                new BigDecimal("10000"), List.of(HOUSE_1));


        EmailEventDto expectedEvent = new EmailEventDto(
                debtor.getEmail(),
                EmailType.SINGLE_DEBTOR_NOTIFICATION,
                Map.of("firstName", debtor.getFirstName(),
                        "lastName", debtor.getLastName(),
                        "debt", String.valueOf(debtor.getTaxesDebt()),
                        "hasChildren", "No",
                        "familyStatus", "Single")
        );

        ownerTestRepository.save(debtor);

        debtorNotificationService.notifyDebtorById(debtor.getId());

        String body = (String) rabbitTemplate.receiveAndConvert(EMAIL_TOPIC);

        assertTrue(Objects.nonNull(body));

        EmailEventDto receivedEvent = mapper.readValue(body, EmailEventDto.class);

        assertEquals(expectedEvent, receivedEvent);
    }

    @Test
    void should_not_send_notification_to_certain_debtor_by_id_if_id_is_wrong() {
        assertThrows(NoSuchOwnerException.class, () -> debtorNotificationService.notifyDebtorById(1));
    }

    @Test
    void should_not_send_notification_to_certain_debtor_by_id_if_the_owner_debt_is_zero() {
        Owner debtor = new Owner(1, "Frank", "John",
                30, FamilyStatus.SINGLE,
                false, "frankjohn@gmail.com",
                "+456987123",
                LocalDate.of(1994, 5, 9),
                new BigDecimal("0"), List.of(HOUSE_1));

        ownerTestRepository.save(debtor);

        assertThrows(DontHaveTaxDebtsException.class, () -> debtorNotificationService.notifyDebtorById(debtor.getId()));
    }
}
