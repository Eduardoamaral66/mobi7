package com.mobi7.restapi.service;

import com.mobi7.restapi.entity.Poi;
import com.mobi7.restapi.repository.PoiRepository;
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
public class PoiService {

    @Autowired
    PoiRepository repository;

    public void save(MultipartFile file) {
        try {
            List<Poi> pois = CSVHelper.csvToPois(file.getInputStream());
            repository.saveAll(pois);
        } catch (IOException e) {
            throw new RuntimeException("fail to store csv data: " + e.getMessage());
        }
    }

    public List<Poi> findAll() {
        return repository.findAll();
    }

    public List<Poi> findByName(String poiName) {
        return repository.findByName(poiName);
    }
}
