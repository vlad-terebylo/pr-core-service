package com.tvo.propertyregister.repository;

import com.tvo.propertyregister.model.Complain;

import java.util.List;

public interface ComplainRepository {

    List<Complain> findAll();

    List<Complain> findByUserId(int userId);

    boolean save(Complain complain);

    boolean update(int id, Complain complain);

    boolean remove(int id);
}
