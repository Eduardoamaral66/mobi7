package com.mobi7.restapi.repository;

import com.mobi7.restapi.entity.Position;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Eduardo
 */
@Repository
public interface PositionRepository extends CrudRepository<Position, Long> {

    @Override
    List<Position> findAll();
}
