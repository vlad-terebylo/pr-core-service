package com.tvo.propertyregister.service;

import com.tvo.propertyregister.model.Complain;
import com.tvo.propertyregister.repository.ComplainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplainService {
    private final ComplainRepository complainRepository;

    public List<Complain> getAllComplains() {
        return this.complainRepository.findAll();
    }

    public List<Complain> getComplainByUserId(int userId) {
        return this.complainRepository.findByUserId(userId);
    }

    public boolean addNewComplain(Complain complain) {
        return this.complainRepository.save(complain);
    }

    public boolean updateComplainInfo(int id, Complain complain) {
        return this.complainRepository.update(id, complain);
    }

    public boolean deleteComplain(int id) {
        return this.complainRepository.remove(id);
    }
}
