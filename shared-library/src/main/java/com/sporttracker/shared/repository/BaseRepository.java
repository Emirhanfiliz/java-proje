package com.sporttracker.shared.repository;

import com.sporttracker.shared.entity.BaseEntity;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;

/**
 * Projedeki tüm MongoDB ve PostgreSQL (JPA) repository interfacelerinin extend edeceği (kalıtım alacağı) temel kalıp.
 * Pagination ve Crud işlemlerini (save, delete, id ile bul vs.) tek seferde Generic olarak sağlar.
 *
 * @param <T>  Entity Tipi (User, Workout etc.)
 * @param <ID> Id Tipi (String or Long)
 */
@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity<ID>, ID extends Serializable>
        extends CrudRepository<T, ID>, PagingAndSortingRepository<T, ID> {

}
