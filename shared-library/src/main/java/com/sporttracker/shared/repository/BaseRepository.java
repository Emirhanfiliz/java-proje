package com.sporttracker.shared.repository;

import com.sporttracker.shared.entity.BaseEntity;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;

/**
 * @param <T>  entity type
 * @param <ID> identifier type
 */
@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity<ID>, ID extends Serializable>
        extends CrudRepository<T, ID>, PagingAndSortingRepository<T, ID> {

}
