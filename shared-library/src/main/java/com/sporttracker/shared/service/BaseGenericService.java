package com.sporttracker.shared.service;

import java.util.List;
import java.util.Optional;


  @param <T>  Entity objesi
  @param <ID> Benzersiz Kimlik

public interface BaseGenericService<T, ID> {

    T save(T entity);

    T update(ID id, T entity);

    void delete(ID id);

    Optional<T> findById(ID id);

    List<T> findAll();

}
