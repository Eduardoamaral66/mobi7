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

    private List<PoiTrackingDTO> tracking;

    public List<PoiTrackingDTO> trackPosition(PoiTrackingFilter filter) {
        tracking = new ArrayList();
        PoiTrackingDTO dto;

        final List<Poi> pois;
        final List<Position> positions;

        prepareFilter(filter);
        pois = loadPois(filter);
        positions = loadPositions(filter);

        for (Position position : positions) {
            String licensePlate = position.getLicensePlate();
            Date positionDate = position.getDate();
            Point positionPoint = getGeoPoint(position);

            for (Poi poi : pois) {
                String poiName = poi.getName();
                Point poiPoint = getGeoPoint(poi);
                Boolean isUnderPoi = isUnderPoi(positionPoint, poiPoint, poi.getRadius());

                if (isUnderPoi) {
                    dto = getOrCreateTrackingDTO(poiName, licensePlate);
                    calculateTime(dto, positionDate);
                    dto.setLastDate(positionDate);
                }
            }
        }

        return tracking;
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
        if (StringUtils.isEmpty(filter.getLicensePlate())) {
            return posistionService.findByDateBetween(filter.getInitDate(), filter.getEndDate());
        } else {
            return posistionService.findByDateBetweenAndLicensePlate(filter.getInitDate(), filter.getEndDate(), filter.getLicensePlate());
        }
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

    private PoiTrackingDTO getOrCreateTrackingDTO(String poiName, String licensePlate) {
        PoiTrackingDTO track;
        track = tracking
                .stream()
                .filter(dto
                        -> dto.getPoiName().equals(poiName)
                && dto.getLicensePlate().equals(licensePlate)
                ).findFirst()
                .orElse(null);

        if (track == null) {
            track = new PoiTrackingDTO();
            track.setLicensePlate(licensePlate);
            track.setPoiName(poiName);
            track.setTime(0L);

            tracking.add(track);
        }
        return track;
    }

    private void calculateTime(PoiTrackingDTO dto, Date actualDateP) {
        if (dto.getLastDate() != null) {
            LocalDateTime lastDate = dto.getLastDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime actualDate = actualDateP.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

            Long actualTime = dto.getTime();
            Long plusTime = SECONDS.between(lastDate, actualDate);

            dto.setTime(actualTime + plusTime);
        }else{
            dto.setFirstDate(actualDateP);
        }
    }
}
