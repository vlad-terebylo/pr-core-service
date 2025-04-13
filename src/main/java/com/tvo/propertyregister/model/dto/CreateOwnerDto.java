package com.tvo.propertyregister.model.dto;

import com.tvo.propertyregister.model.owner.FamilyStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateOwnerDto
        (
                String firstName,
                String lastName,
                int age,
                FamilyStatus familyStatus,
                boolean hasChildren,
                String email,
                String phoneNumber,
                LocalDate birthday,
                BigDecimal taxesDebt
        ) {
}
