package com.tvo.propertyregister.controller;

import com.tvo.propertyregister.model.BooleanResponseDto;
import com.tvo.propertyregister.model.Complain;
import com.tvo.propertyregister.service.ComplainService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/complains")
public class ComplainController {
    private final ComplainService complainService;

    @GetMapping
    public ResponseEntity<List<Complain>> getAllComplains() {
        return ResponseEntity.ok(this.complainService.getAllComplains());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Complain>> getComplainByUserId(@PathVariable int userId) {
        return ResponseEntity.ok(this.complainService.getComplainByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<BooleanResponseDto> addNewComplain(@RequestBody Complain complain) {
        return ResponseEntity.ok(new BooleanResponseDto(this.complainService.addNewComplain(complain)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BooleanResponseDto> updateComplainInfo(@PathVariable int id, @RequestBody Complain complain) {
        return ResponseEntity.ok(new BooleanResponseDto(this.complainService.updateComplainInfo(id, complain)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BooleanResponseDto> deleteComplain(@PathVariable int id) {
        return ResponseEntity.ok(new BooleanResponseDto(this.complainService.deleteComplain(id)));
    }

}
