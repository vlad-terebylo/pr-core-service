package com.tvo.propertyregister.service;

import com.tvo.propertyregister.exception.DontHaveTaxDebtsException;
import com.tvo.propertyregister.exception.NoDebtorsInDebtorListException;
import com.tvo.propertyregister.model.dto.EmailEventDto;
import com.tvo.propertyregister.model.dto.EmailType;
import com.tvo.propertyregister.model.owner.Owner;
import com.tvo.propertyregister.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class DebtorNotificationService {

    private final OwnerRepository ownerRepository;
    private final EmailSender emailSender;

    public boolean notifyAllDebtors() {
        List<Owner> debtors = this.ownerRepository.findDebtors();
        Map<String, String> params = new HashMap<>();
        params.put("numberOfDebtors", String.valueOf(debtors.size()));

        if (debtors.isEmpty()) {
            throw new NoDebtorsInDebtorListException("No debtors in debtor list");
        }

        for (Owner debtor : debtors) {
            params.put("firstName", debtor.getFirstName());
            params.put("lastName", debtor.getLastName());
            params.put("debt", String.valueOf(debtor.getTaxesDept()));

            EmailEventDto emailEvent = new EmailEventDto(
                    debtor.getEmail(),
                    EmailType.ALL_DEBTOR_NOTIFICATION,
                    params
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

        String hasChildren = debtor.isHasChildren() ? "Yes" : "No";
        String familyStatus = String.valueOf(debtor.getFamilyStatus()).charAt(0) + String.valueOf(debtor.getFamilyStatus()).toLowerCase().substring(1);

        EmailEventDto emailEvent = new EmailEventDto(
                debtor.getEmail(),
                EmailType.SINGLE_DEBTOR_NOTIFICATION,
                Map.of("firstName", debtor.getFirstName(),
                        "lastName", debtor.getLastName(),
                        "debt", String.valueOf(debtor.getTaxesDept()),
                        "hasChildren", hasChildren,
                        "familyStatus", familyStatus)
        );


        emailSender.send(emailEvent);

        return true;
    }
}
