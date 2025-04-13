package com.tvo.propertyregister.service;

import com.tvo.propertyregister.exception.DontHaveTaxDebtsException;
import com.tvo.propertyregister.exception.NoDebtorsInDebtorListException;
import com.tvo.propertyregister.exception.NoSuchOwnerException;
import com.tvo.propertyregister.model.dto.EmailEventDto;
import com.tvo.propertyregister.model.dto.EmailType;
import com.tvo.propertyregister.model.owner.Owner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class DebtorNotificationService {

    private final OwnerService ownerService;
    private final EmailSender emailSender;

    public boolean notifyAllDebtors() {
        List<Owner> debtors = this.ownerService.findDebtors();
        Map<String, String> params = new HashMap<>();
        params.put("numberOfDebtors", String.valueOf(debtors.size()));

        if (debtors.isEmpty()) {
            throw new NoDebtorsInDebtorListException("No debtors in debtor list");
        }

        for (Owner debtor : debtors) {
            params.put("firstName", debtor.getFirstName());
            params.put("lastName", debtor.getLastName());
            params.put("debt", String.valueOf(debtor.getTaxesDebt()));

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
        Owner debtor = this.ownerService.getOwnerById(id);
        if(Objects.isNull(debtor)){
            throw new NoSuchOwnerException("The owner with id " + id + " does not exists");
        }
        if (debtor.getTaxesDebt().compareTo(new BigDecimal("0")) <= 0) {
            throw new DontHaveTaxDebtsException("Does not exists or his tax debt is lower or equals zero!");
        }

        String hasChildren = debtor.isHasChildren() ? "Yes" : "No";
        String familyStatus = String.valueOf(debtor.getFamilyStatus()).charAt(0) + String.valueOf(debtor.getFamilyStatus()).toLowerCase().substring(1);

        EmailEventDto emailEvent = new EmailEventDto(
                debtor.getEmail(),
                EmailType.SINGLE_DEBTOR_NOTIFICATION,
                Map.of("firstName", debtor.getFirstName(),
                        "lastName", debtor.getLastName(),
                        "debt", String.valueOf(debtor.getTaxesDebt()),
                        "hasChildren", hasChildren,
                        "familyStatus", familyStatus)
        );


        emailSender.send(emailEvent);

        return true;
    }
}
