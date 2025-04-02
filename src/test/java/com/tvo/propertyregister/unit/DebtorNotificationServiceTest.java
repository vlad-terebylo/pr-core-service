package com.tvo.propertyregister.unit;

import com.tvo.propertyregister.exception.DontHaveTaxDebtsException;
import com.tvo.propertyregister.exception.NoDebtorsInDebtorListException;
import com.tvo.propertyregister.exception.NoSuchOwnerException;
import com.tvo.propertyregister.model.dto.EmailEventDto;
import com.tvo.propertyregister.model.dto.EmailType;
import com.tvo.propertyregister.model.owner.FamilyStatus;
import com.tvo.propertyregister.model.owner.Owner;
import com.tvo.propertyregister.service.DebtorNotificationService;
import com.tvo.propertyregister.service.EmailSender;
import com.tvo.propertyregister.service.OwnerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DebtorNotificationServiceTest {
    @Mock
    private OwnerService ownerService;

    @Mock
    private EmailSender emailSender;

    @InjectMocks
    private DebtorNotificationService debtorNotificationService;

    private final Owner debtor = new Owner(
            1,
            "terebylov@ssemi.cz",
            "John",
            "Doe",
            new BigDecimal("100.0"),
            false,
            FamilyStatus.SINGLE);

    @Test
    void should_notify_all_debtors_when_there_is_one_debtor() {
        Map<String, String> params = Map.of(
                "firstName", debtor.getFirstName(),
                "lastName", debtor.getLastName(),
                "debt", String.valueOf(debtor.getTaxesDept()),
                "numberOfDebtors", String.valueOf(1));

        EmailEventDto expectedEmailDto = new EmailEventDto(
                debtor.getEmail(),
                EmailType.ALL_DEBTOR_NOTIFICATION,
                params);

        when(ownerService.findDebtors()).thenReturn(List.of(debtor));

        boolean result = debtorNotificationService.notifyAllDebtors();

        verify(emailSender, times(1)).send(eq(expectedEmailDto));
        assertTrue(result);
    }

    @Test
    void should_notify_debtors_when_there_are_two_or_more_debtors() {
        Owner debtor2 = new Owner(
                2,
                "terebylov@ssemi.cz",
                "Frank",
                "John",
                new BigDecimal("10000"),
                false,
                FamilyStatus.SINGLE
        );

        List<Owner> debtors = List.of(debtor, debtor2);

        when(ownerService.findDebtors()).thenReturn(debtors);

        boolean result = debtorNotificationService.notifyAllDebtors();

        verify(emailSender, times(2)).send(any(EmailEventDto.class));
        assertTrue(result);
    }

    @Test
    void should_notify_all_debtors_when_no_debtors() {
        when(ownerService.findDebtors()).thenReturn(List.of());

        assertThrows(NoDebtorsInDebtorListException.class, () -> debtorNotificationService.notifyAllDebtors());

        verify(emailSender, never()).send(any());
    }

    @Test
    void should_notify_debtor_by_id() {
        String hasChildren = debtor.isHasChildren() ? "Yes" : "No";
        String familyStatus = String.valueOf(debtor.getFamilyStatus()).charAt(0) + String.valueOf(debtor.getFamilyStatus()).toLowerCase().substring(1);

        Map<String, String> params = Map.of(
                "firstName", debtor.getFirstName(),
                "lastName", debtor.getLastName(),
                "debt", String.valueOf(debtor.getTaxesDept()),
                "hasChildren", hasChildren,
                "familyStatus", familyStatus);

        EmailEventDto expectedEmailDto = new EmailEventDto(
                debtor.getEmail(),
                EmailType.SINGLE_DEBTOR_NOTIFICATION,
                params);
        when(ownerService.getOwnerById(1)).thenReturn(debtor);

        boolean result = debtorNotificationService.notifyDebtorById(1);

        verify(emailSender, times(1)).send(eq(expectedEmailDto));
        assertTrue(result);
    }

    @Test
    void should_not_notify_debtor_by_id_when_owner_has_no_debt() {
        debtor.setTaxesDept(new BigDecimal("0"));
        when(ownerService.getOwnerById(1)).thenReturn(debtor);

        assertThrows(DontHaveTaxDebtsException.class, () -> debtorNotificationService.notifyDebtorById(1));

        verify(emailSender, never()).send(any());
    }

    @Test
    void should_not_notify_debtor_by_id_if_id_is_wrong() {
        int invalidId = -1;

        when(ownerService.getOwnerById(invalidId)).thenThrow(new NoSuchOwnerException("The owner with id " + invalidId + " does not exists"));

        assertThrows(NoSuchOwnerException.class, () -> debtorNotificationService.notifyDebtorById(invalidId));

        verify(emailSender, never()).send(any());
    }
}
