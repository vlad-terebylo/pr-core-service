package com.tvo.propertyregister.service;

import com.tvo.propertyregister.exception.DontHaveTaxDebtsException;
import com.tvo.propertyregister.model.dto.EmailEventDto;
import com.tvo.propertyregister.model.dto.EmailType;
import com.tvo.propertyregister.model.owner.Owner;
import com.tvo.propertyregister.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
@RequiredArgsConstructor
public class DebtorNotificationService {

    private final OwnerRepository ownerRepository;
    private final EmailSender emailSender;

    public boolean notifyAllDebtors() {
        List<Owner> debtors = this.ownerRepository.findDebtors();

        for (Owner debtor : debtors) {
            EmailEventDto emailEvent = new EmailEventDto(
                    debtor.getEmail(),
                    debtor.getFirstName(),
                    debtor.getLastName(),
                    debtor.getTaxesDept(),
                    EmailType.ALL_DEBTOR_NOTIFICATION
            );

            emailSender.send(emailEvent);
        }

        return true;
    }

    public boolean notifyDebtorById(int id) {
        Owner debtor = this.ownerRepository.findById(id);
        if (debtor.getTaxesDept().compareTo(new BigDecimal("0")) <= 0) {
            throw new DontHaveTaxDebtsException("Does not exists or his tax debt is lower or equals zero!");
        }

        EmailEventDto emailEvent = new EmailEventDto(
                debtor.getEmail(),
                debtor.getFirstName(),
                debtor.getLastName(),
                debtor.getTaxesDept(),
                EmailType.SINGLE_DEBTOR_NOTIFICATION
        );

        emailSender.send(emailEvent);

        return true;
    }
}
