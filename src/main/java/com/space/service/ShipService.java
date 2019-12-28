package com.space.service;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;

@Service
public class ShipService {

    @Autowired
    private final ShipRepository shipRepository;

    public ShipService(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    public void createShip(Ship ship) {
        updateRating(ship);
        shipRepository.save(ship);
    }

    public List<Ship> findAll() {return shipRepository.findAll();}

    public Page<Ship> findFiltered(Specification filter, Pageable pageable){

        return  shipRepository.findAll(filter, pageable);
    }

    public Page<Ship> findPaged(Pageable pageable){

        return  shipRepository.findAll(pageable);
    }

    public Ship getById(Long id) {return shipRepository.getById(id);}

    public boolean deleteShip(Long id) {
        if (!shipRepository.existsById(id))
            return false;
        shipRepository.deleteById(id);
        return true;
    }

    public boolean exists(Long id){
        return shipRepository.existsById(id);
    }

    public Ship editShip(Long id, Ship ship){

        Ship oldShip = shipRepository.getById(id);

        if (ship == null || ship.getName() == null && ship.getPlanet() == null && ship.getShipType() == null &&
                ship.getProdDate() == null && ship.getUsed() == null && ship.getSpeed() == null && ship.getCrewSize() == null){
            return oldShip;
        }
        if(ship.getName()!= null && !ship.getName().equals(""))
            oldShip.setName(ship.getName());
        if(ship.getPlanet()!= null && !ship.getPlanet().equals(""))
            oldShip.setPlanet(ship.getPlanet());
        if(ship.getShipType()!= null)
            oldShip.setShipType(ShipType.valueOf(ship.getShipType()));
        if(ship.getProdDate()!= null)
            oldShip.setProdDate(ship.getProdDate());
        if(ship.getUsed()!= null)
            oldShip.setUsed(ship.getUsed());
        if(ship.getSpeed()!= null)
            oldShip.setSpeed(ship.getSpeed());
        if(ship.getCrewSize()!= null)
            oldShip.setCrewSize(ship.getCrewSize());

        oldShip.setId(id);
        updateRating(oldShip);
        shipRepository.save(oldShip);
        return oldShip;
    }

    private static void updateRating(Ship ship){
        double usedRaiting = 1d;
        if (ship.getUsed())
            usedRaiting = 0.5d;
        Calendar cal = Calendar.getInstance();
        cal.setTime(ship.getProdDate());
        double rating = (80.0d * ship.getSpeed() * usedRaiting) / (3019 - cal.get(Calendar.YEAR) + 1);
        ship.setRating(rating);
    }

}
