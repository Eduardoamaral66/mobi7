package com.mobi7.restapi.service;

import com.mobi7.restapi.entity.Poi;
import com.mobi7.restapi.entity.Position;
import com.mobi7.restapi.repository.PoiRepository;
import com.mobi7.restapi.repository.PositionRepository;
import com.mobi7.restapi.utils.CSVHelper;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Eduardo
 */
@Service
public class PositionService {

    @Autowired
    PositionRepository repository;

    public void save(MultipartFile file) {
        try {
            List<Position> positions = CSVHelper.csvToPosition(file.getInputStream());
            repository.saveAll(positions);
        } catch (IOException e) {
            throw new RuntimeException("fail to store csv data: " + e.getMessage());
        }
    }

    public List<Position> getAllPositions() {
        return repository.findAll();
    }
}
