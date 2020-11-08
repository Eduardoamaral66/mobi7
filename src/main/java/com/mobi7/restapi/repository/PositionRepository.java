package com.mobi7.restapi.repository;

import com.mobi7.restapi.entity.Position;
import java.util.Date;
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

    List<Position> findByLicensePlate(String licensePlate);

    List<Position> findByDateBetween(Date begin, Date end);

    List<Position> findByDateBetweenAndLicensePlateIgnoreCase(Date begin, Date end, String licensePlate);
}
