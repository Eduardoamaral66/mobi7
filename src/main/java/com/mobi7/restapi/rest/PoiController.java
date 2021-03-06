package com.mobi7.restapi.rest;

import com.mobi7.restapi.entity.Poi;
import com.mobi7.restapi.repository.PoiRepository;
import com.mobi7.restapi.response.ResponseMessage;
import com.mobi7.restapi.service.PoiService;
import com.mobi7.restapi.utils.CSVHelper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Eduardo
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/poi")
public class PoiController {

    private static final Logger log = LoggerFactory.getLogger(PoiController.class);

    @Autowired
    private PoiService poiService;

    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {
        String message = "";

        if (CSVHelper.hasCSVFormat(file)) {
            try {
                poiService.save(file);
                message = "Uploaded the file successfully: " + file.getOriginalFilename();
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
            } catch (Exception e) {
                message = "Could not upload the file: " + file.getOriginalFilename() + " because of: " + e.getMessage();
                log.error(message, e);
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
            }
        }

        message = "Please upload a csv file!";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
    }

    @GetMapping("/list")
    public ResponseEntity<List<Poi>> getPois() {
        List<Poi> pois = poiService.findAll();

        if (CollectionUtils.isEmpty(pois)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(pois, HttpStatus.OK);
    }
}
