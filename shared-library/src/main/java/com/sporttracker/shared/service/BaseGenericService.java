package com.sporttracker.shared.service;

import java.util.List;
import java.util.Optional;

/**
 * Temel Servis Operasyonlarını (CRUD) içeren Generic Arayüz.
 * Tüm servislerde tekrarlanan (save, delete, findById) metodlarının her servise yeniden yazılmasını önler.
 *
 * @param <T>  Entity objesi
 * @param <ID> Benzersiz Kimlik
 */
public interface BaseGenericService<T, ID> {

    T save(T entity);

    T update(ID id, T entity);

    void delete(ID id);

    Optional<T> findById(ID id);

    List<T> findAll();

}
