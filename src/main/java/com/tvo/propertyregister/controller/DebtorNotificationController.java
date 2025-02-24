package com.tvo.propertyregister.controller;

import com.tvo.propertyregister.model.dto.BooleanResponseDto;
import com.tvo.propertyregister.service.DebtorNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/debtors/notify")
public class DebtorNotificationController {

    private final DebtorNotificationService debtorNotificationService;

    @PostMapping
    public ResponseEntity<BooleanResponseDto> notifyAllDebtors() {
        return ResponseEntity.ok(new BooleanResponseDto(debtorNotificationService.enqueueDebtorNotifications()));
    }

    @PostMapping("/{id}")
    public ResponseEntity<BooleanResponseDto> notifyDebtorById(@PathVariable int id){
        return ResponseEntity.ok(new BooleanResponseDto(debtorNotificationService.notifyDebtorById(id)));
    }

}
