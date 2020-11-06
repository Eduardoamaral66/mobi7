package com.mobi7.restapi.rest;

import com.mobi7.restapi.dto.PoiTrackingDTO;
import com.mobi7.restapi.entity.Poi;
import com.mobi7.restapi.filter.PoiTrackingFilter;
import com.mobi7.restapi.service.PoiTrackingService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Eduardo
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/poi_tracking")
public class PoiTrackingController {

    @Autowired
    private PoiTrackingService service;

    @PostMapping("/track")
    public ResponseEntity<List<PoiTrackingDTO>> search(@RequestBody PoiTrackingFilter filter) {
        List<PoiTrackingDTO> result = service.trackPosition(filter);

        if (CollectionUtils.isEmpty(result)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
