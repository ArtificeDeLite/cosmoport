package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class ShipController {

    @Autowired
    private ShipService shipService;

    private Integer counter = 0;

    @GetMapping(value = "/rest/ships")
    public List<Ship> getFilteredShips(@RequestParam(defaultValue = "3") Integer pageSize,
                                       @RequestParam(defaultValue = "0") Integer pageNumber,
                                       @RequestParam(defaultValue = "ID") ShipOrder order,
                                       @RequestParam(required = false) String name,
                                       @RequestParam(required = false) String planet,
                                       @RequestParam(required = false) ShipType shipType,
                                       @RequestParam(required = false) Long after,
                                       @RequestParam(required = false) Long before,
                                       @RequestParam(required = false) Boolean isUsed,
                                       @RequestParam(required = false) Double minSpeed,
                                       @RequestParam(required = false) Double maxSpeed,
                                       @RequestParam(required = false) Integer minCrewSize,
                                       @RequestParam(required = false) Integer maxCrewSize,
                                       @RequestParam(required = false) Double minRating,
                                       @RequestParam(required = false) Double maxRating) {


        PageRequest request = PageRequest.of(pageNumber,pageSize, Sort.by( order.getFieldName()));

        Specification<Ship> specification;
        Page page;

        ShipSpecificationsBuilder builder = new ShipSpecificationsBuilder();

        boolean filtered = name != null || planet != null || shipType != null || after != null ||
                before != null || isUsed != null || minSpeed != null || maxSpeed != null ||
                minCrewSize != null || maxCrewSize != null || minRating != null || maxRating != null;

        if (filtered){
            if (name != null)
                builder.with("name", ":", name);
            if (planet != null)
                builder.with("planet", ":", planet);
            if (minSpeed != null)
                builder.with("speed", ">", minSpeed);
            if (maxSpeed != null)
                builder.with("speed", "<", maxSpeed);
            if (minCrewSize != null)
                builder.with("crewSize", ">", minCrewSize);
            if (maxCrewSize != null)
                builder.with("crewSize", "<", maxCrewSize);
            if (minRating != null)
                builder.with("rating", ">", minRating);
            if (maxRating != null)
                builder.with("rating", "<", maxRating);
            if (isUsed != null) {
                if (isUsed)
                    builder.with("isUsed", ":", true);
                else
                    builder.with("isUsed", ":", false);
            }
            if (shipType != null) {
                builder.with("shipType", ":", shipType);
            }
            if (after != null) {
                builder.with("prodDate", "d>", new java.sql.Date(after));
            }
            if (before != null) {
                builder.with("prodDate", "d<", new java.sql.Date(before));
            }

            specification = builder.build();

            page = shipService.findFiltered(specification, request);
        } else {
            page = shipService.findPaged(request);
        }
        List<Ship> list = page.getContent();

        counter = (int) page.getTotalElements();
        return list;
    }

    @GetMapping(value = "/rest/ships/{id}")
    public ResponseEntity<Ship> getShipById(@PathVariable Long id) {
        if (id == null || id < 1)
            return ResponseEntity.status(400).build();
        if (!shipService.exists(id))
            return ResponseEntity.status(404).build();
        return ResponseEntity.ok(shipService.getById(id));
    }

    @GetMapping(value = "/rest/ships/count")
    public ResponseEntity<Integer> getCount(@RequestParam(defaultValue = "3") Integer pageSize,
                @RequestParam(defaultValue = "0") Integer pageNumber,
                @RequestParam(defaultValue = "ID") ShipOrder order,
                @RequestParam(required = false) String name,
                @RequestParam(required = false) String planet,
                @RequestParam(required = false) ShipType shipType,
                @RequestParam(required = false) Long after,
                @RequestParam(required = false) Long before,
                @RequestParam(required = false) Boolean isUsed,
                @RequestParam(required = false) Double minSpeed,
                @RequestParam(required = false) Double maxSpeed,
                @RequestParam(required = false) Integer minCrewSize,
                @RequestParam(required = false) Integer maxCrewSize,
                @RequestParam(required = false) Double minRating,
                @RequestParam(required = false) Double maxRating){

        List<Ship> list = getFilteredShips(pageSize, pageNumber, order, name, planet, shipType, after,
                before, isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);

        return  ResponseEntity.ok(counter);
    }

    @PostMapping(value = "rest/ships/")
    public ResponseEntity<Ship> addShip(@RequestBody Ship ship) {
        if (ship.getUsed() == null)
            ship.setUsed(false);

        if (ship.getName() == null || ship.getPlanet() == null ||
                ship.getShipType() == null || ship.getProdDate() == null ||
                ship.getSpeed() == null || ship.getCrewSize() == null) {
            return ResponseEntity.status(400).build();
        }
        if (ship.getName().length() > 50 || ship.getName().equals("") ||
                ship.getPlanet().length() > 50 || ship.getPlanet().equals(""))
            return ResponseEntity.status(400).build();

        if (ship.getCrewSize() < 1 || ship.getCrewSize() > 9999 ||
                ship.getSpeed() < 0.01 || ship.getSpeed() > 0.99 ||
                ship.getProdDate().getTime() < 0L)
            return ResponseEntity.status(400).build();

        Calendar calAfter = Calendar.getInstance();
        Calendar calBefore = Calendar.getInstance();
        calAfter.set(2800, Calendar.JANUARY,1,0,0,0);
        calBefore.set(3019,Calendar.DECEMBER,31,23,59,59);

        if (ship.getProdDate().after(calBefore.getTime()) || ship.getProdDate().before(calAfter.getTime()) )
            return ResponseEntity.status(400).build();

        shipService.createShip(ship);
        return ResponseEntity.ok(ship);
    }

    @PostMapping(value = "rest/ships/{id}")
    public ResponseEntity<Ship> editShip(@PathVariable Long id, @RequestBody Ship ship) {
        Date before = new GregorianCalendar(2800, 0,1).getTime();
        Date after = new GregorianCalendar(3019,11,31).getTime();

        if (id == null || id <= 0 || ship == null ||
                (ship.getName() != null && ship.getName().equals("")) ||
                (ship.getPlanet() != null && ship.getPlanet().equals("")) ||
                (ship.getCrewSize() != null && (ship.getCrewSize() < 1 || ship.getCrewSize() > 9999)) ||
                (ship.getProdDate() != null && (ship.getProdDate().before(before) || ship.getProdDate().after(after))))
            return ResponseEntity.status(400).build();
        if (!shipService.exists(id))
            return ResponseEntity.status(404).build();
        Ship newShip;
        newShip = shipService.editShip(id, ship);
        return ResponseEntity.ok(newShip);
    }

    @DeleteMapping(value = "rest/ships/{id}")
    public ResponseEntity<Void> deleteShip(@PathVariable Long id) {
        if (id == null || id < 1)
            return ResponseEntity.status(400).build();
        if (!shipService.deleteShip(id))
            return ResponseEntity.status(404).build();
        return ResponseEntity.ok().build();
    }

}
