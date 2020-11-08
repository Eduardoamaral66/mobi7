package com.mobi7.restapi.service;

import static java.time.temporal.ChronoUnit.SECONDS;
import com.grum.geocalc.Coordinate;
import com.grum.geocalc.EarthCalc;
import com.grum.geocalc.Point;
import com.mobi7.restapi.dto.PoiTrackingDTO;
import com.mobi7.restapi.entity.GeoLocation;
import com.mobi7.restapi.entity.Poi;
import com.mobi7.restapi.entity.Position;
import com.mobi7.restapi.filter.PoiTrackingFilter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 *
 * @author Eduardo
 */
@Service
public class PoiTrackingService {

    @Autowired
    private PoiService poiService;

    @Autowired
    private PositionService posistionService;

    private List<PoiTrackingDTO> trackingInProcess;
    private List<PoiTrackingDTO> trackingClosed;

    public List<PoiTrackingDTO> trackPosition(PoiTrackingFilter filter) {
        trackingInProcess = new ArrayList();
        trackingClosed = new ArrayList<>();

        final List<Poi> pois;
        final List<Position> positions;

        prepareFilter(filter);
        pois = loadPois(filter);
        positions = loadPositions(filter);
        calculeTracking(positions, pois);

        for (PoiTrackingDTO poiTrackingDTO : trackingClosed) {
            calculateTime(poiTrackingDTO);
        }

        return trackingClosed;
    }

    private void prepareFilter(PoiTrackingFilter filter) {
        if (filter.getInitDate() == null) {
            filter.setInitDate(new Date(0L));
        }
        if (filter.getEndDate() == null) {
            filter.setEndDate(new Date());
        }
    }

    private List<Poi> loadPois(PoiTrackingFilter filter) {
        if (StringUtils.isEmpty(filter.getPoiName())) {
            return poiService.findAll();
        } else {
            return poiService.findByName(filter.getPoiName());
        }
    }

    private List<Position> loadPositions(PoiTrackingFilter filter) {
        List<Position> positions;
        if (StringUtils.isEmpty(filter.getLicensePlate())) {
            positions = posistionService.findByDateBetween(filter.getInitDate(), filter.getEndDate());
        } else {
            positions = posistionService.findByDateBetweenAndLicensePlate(filter.getInitDate(), filter.getEndDate(), filter.getLicensePlate());
        }
        Collections.sort(positions);

        return positions;
    }

    private Point getGeoPoint(GeoLocation geo) {
        Coordinate lat = Coordinate.fromDegrees(Double.parseDouble(geo.getLatitude()));
        Coordinate lng = Coordinate.fromDegrees(Double.parseDouble(geo.getLongitude()));
        return Point.at(lat, lng);
    }

    private Boolean isUnderPoi(Point positionPoint, Point poiPoint, Integer radius) {
        Double distance = EarthCalc.gcd.distance(positionPoint, poiPoint);

        return radius.doubleValue() >= distance;
    }

    private PoiTrackingDTO getTrackingDTOInProcess(String poiName, String licensePlate) {
        return trackingInProcess
                .stream()
                .filter(dto
                        -> dto.getPoiName().equals(poiName)
                && dto.getLicensePlate().equals(licensePlate)
                ).findFirst()
                .orElse(null);
    }

    private PoiTrackingDTO getOrCreateTrackingDTO(String poiName, String licensePlate) {
        PoiTrackingDTO track;
        track = getTrackingDTOInProcess(poiName, licensePlate);

        if (track == null) {
            track = new PoiTrackingDTO();
            track.setLicensePlate(licensePlate);
            track.setPoiName(poiName);
            track.setTime(0L);

            trackingInProcess.add(track);
        }
        return track;
    }

    private void calculateTime(PoiTrackingDTO dto) {
        if (dto.getFirstDate() == null) {
            throw new RuntimeException("Tracking without firstDate");
        }
        if (dto.getLastDate() == null) {
            throw new RuntimeException("Tracking without lastDate");
        }

        LocalDateTime firstDate = dto.getFirstDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime lastDate = dto.getLastDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        Long time = SECONDS.between(firstDate, lastDate);
        dto.setTime(time);
    }

    private void calculeTracking(List<Position> positions, List<Poi> pois) {
        PoiTrackingDTO dto;

        for (Poi poi : pois) {
            String poiName = poi.getName();
            Point poiPoint = getGeoPoint(poi);

            for (Position position : positions) {
                String licensePlate = position.getLicensePlate();
                Date positionDate = position.getDate();
                Point positionPoint = getGeoPoint(position);

                Boolean isUnderPoi = isUnderPoi(positionPoint, poiPoint, poi.getRadius());

                if (isUnderPoi) {
                    dto = getOrCreateTrackingDTO(poiName, licensePlate);
                    if (dto.getFirstDate() == null) {
                        dto.setFirstDate(positionDate);
                    }
                    dto.setLastDate(positionDate);
                } else {
                    dto = getTrackingDTOInProcess(poiName, licensePlate);
                    if (dto != null) {
                        trackingInProcess.remove(dto);
                        trackingClosed.add(dto);
                    }
                }

            }
        }
        trackingClosed.addAll(trackingInProcess);
    }
}
