package com.mobi7.restapi.utils;

import com.mobi7.restapi.entity.Poi;
import com.mobi7.restapi.entity.Position;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Eduardo
 */
public class CSVHelper {

    private static final Logger log = LoggerFactory.getLogger(CSVHelper.class);

    public static String TYPE = "text/csv";
    static String[] HEADERs = {"Id", "Title", "Description", "Published"};

    public static boolean hasCSVFormat(MultipartFile file) {

        if (!TYPE.equals(file.getContentType())) {
            return false;
        }

        return true;
    }

    public static List<Poi> csvToPois(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

            List<Poi> pois = new ArrayList<>();

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                String name = csvRecord.get("nome");
                Integer radius = Integer.parseInt(csvRecord.get("raio"));
                String latitude = csvRecord.get("latitude");
                String longitude = csvRecord.get("longitude");

                Poi poi = new Poi();
                poi.setName(name);
                poi.setRadius(radius);
                poi.setLatitude(latitude);
                poi.setLongitude(longitude);

                pois.add(poi);
            }

            return pois;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }

    public static List<Position> csvToPosition(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

            DateFormat parser = new SimpleDateFormat("EE MMM d y H:m:s 'GMT'Z (zz)");

            List<Position> positions = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                String plate = csvRecord.get("placa");
                Integer velocity = Integer.parseInt(csvRecord.get("velocidade"));
                String longitude = csvRecord.get("longitude");
                String latitude = csvRecord.get("latitude");
                Boolean ignition = Boolean.parseBoolean(csvRecord.get("ignicao"));

                Date date = new Date();
                try {
                    date = parser.parse(csvRecord.get("data_posicao"));
                } catch (ParseException ex) {
                    log.error("Erro no formato da data", ex);
                }

                Position position = new Position();
                position.setLicensePlate(plate);
                position.setVelocity(velocity);
                position.setIgnition(ignition);
                position.setLatitude(latitude);
                position.setLongitude(longitude);
                position.setDate(date);

                positions.add(position);
            }
            return positions;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }
}
