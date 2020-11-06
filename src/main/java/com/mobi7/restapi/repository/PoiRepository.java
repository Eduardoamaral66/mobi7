package com.mobi7.restapi.repository;

import com.mobi7.restapi.entity.Poi;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Eduardo
 */
@Repository
public interface PoiRepository extends CrudRepository<Poi, Long> {

    @Override
    List<Poi> findAll();
}
