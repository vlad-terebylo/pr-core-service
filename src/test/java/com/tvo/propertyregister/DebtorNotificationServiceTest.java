package com.tvo.propertyregister;

import com.tvo.propertyregister.exception.DontHaveTaxDebtsException;
import com.tvo.propertyregister.model.dto.EmailEventDto;
import com.tvo.propertyregister.model.owner.Owner;
import com.tvo.propertyregister.repository.OwnerRepository;
import com.tvo.propertyregister.service.DebtorNotificationService;
import com.tvo.propertyregister.service.EmailSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DebtorNotificationServiceTest {
    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private EmailSender emailSender;

    @InjectMocks
    private DebtorNotificationService debtorNotificationService;

    private Owner debtor;

    @BeforeEach
    void setUp() {
        debtor = new Owner();
        debtor.setId(1);
        debtor.setEmail("terebylov@ssemi.cz");
        debtor.setFirstName("John");
        debtor.setLastName("Doe");
        debtor.setTaxesDept(new BigDecimal("100.00"));
    }

    @Test
    void should_notify_all_debtors() {
        when(ownerRepository.findDebtors()).thenReturn(List.of(debtor));

        boolean result = debtorNotificationService.notifyAllDebtors();

        verify(emailSender, times(1)).send(any(EmailEventDto.class));
        assertTrue(result);
    }

    @Test
    void should_notify_all_debtors_when_no_debtors() {
        when(ownerRepository.findDebtors()).thenReturn(Collections.emptyList());

        boolean result = debtorNotificationService.notifyAllDebtors();

        verify(emailSender, never()).send(any(EmailEventDto.class));
        assertTrue(result);
    }

    @Test
    void should_notify_debtor_by_id() {
        when(ownerRepository.findById(1)).thenReturn(debtor);

        boolean result = debtorNotificationService.notifyDebtorById(1);

        verify(emailSender, times(1)).send(any(EmailEventDto.class));
        assertTrue(result);
    }

    @Test
    void should_notify_debtor_by_id_when_owner_has_no_debt() {
        debtor.setTaxesDept(new BigDecimal("0"));
        when(ownerRepository.findById(1)).thenReturn(debtor);

        assertThrows(DontHaveTaxDebtsException.class, () -> debtorNotificationService.notifyDebtorById(1));
    }
}
