package com.tvo.propertyregister.repository.inmemory;

import com.tvo.propertyregister.model.Complain;
import com.tvo.propertyregister.repository.ComplainRepository;

import java.util.ArrayList;
import java.util.List;

public class InMemoryComplainRepository implements ComplainRepository {

    private static int counter = 1;
    private static final List<Complain> complains = new ArrayList<>();

    @Override
    public List<Complain> findAll() {
        return complains;
    }

    @Override
    public List<Complain> findByUserId(int userId) {
        return complains.stream()
                .filter(complain -> complain.getUserId() == userId)
                .toList();
    }

    @Override
    public boolean save(Complain complain) {
        complain.setId(counter++);
        return complains.add(complain);
    }

    @Override
    public boolean update(int id, Complain complain) {
        for (Complain currentComplain : complains) {
            if (currentComplain.getId() == id) {
                currentComplain.setText(complain.getText());
                currentComplain.setSubject(complain.getSubject());
                currentComplain.setUserId(complain.getUserId());
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean remove(int id) {
        for (Complain currentComplain : complains) {
            if (currentComplain.getId() == id) {
                complains.remove(currentComplain);
                return true;
            }
        }

        return false;
    }
}
