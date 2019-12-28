package com.space.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;

@Entity
@Table(name = "ship")
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //ID корабля
    @Column
    private String name; //Название корабля (до 50 знаков включительно)
    @Column
    private String planet; // Планета пребывания (до 50 знаков включительно)
    @Column
    @Enumerated(EnumType.STRING)
    private ShipType shipType; // Тип корабля
    @Column
    @Temporal(TemporalType.DATE)
    private Date prodDate; // Дата выпуска. Диапазон значений года 2800..3019 включительно
    @Column
    private Boolean isUsed; //Использованный / новый
    @Column
    private Double speed; //Максимальная скорость корабля. Диапазон значений 0,01..0,99 включительно. Используй математическое округление до сотых.
    @Column
    private Integer crewSize; //Количество членов экипажа. Диапазон значений 1..9999 включительно.
    @Column
    private Double rating; //Рейтинг корабля. Используй математическое округление до сотых.

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name.length() <= 50)
            this.name = name;
    }

    public String getPlanet() {
        return planet;
    }

    public void setPlanet(String planet) {
        if (planet.length() <= 50)
            this.planet = planet;
    }

    public String getShipType() {
        if (shipType == null)
            return null;
        return shipType.toString();
    }

    public void setShipType(ShipType shipType) {
        this.shipType = shipType;
    }

    public Date getProdDate() {
        return prodDate;
    }

    public void setProdDate(Date prodDate) {
        Calendar calendarAfter = Calendar.getInstance();
        calendarAfter.set(2800,Calendar.JANUARY,1,0,0,0);
        Calendar calendarBefore = Calendar.getInstance();
        calendarBefore.set(3019, Calendar.DECEMBER,31,23,59,59);

        if(prodDate.after(calendarAfter.getTime()) && prodDate.before(calendarBefore.getTime()))
            this.prodDate = prodDate;
    }

    public Boolean getUsed() {
        return isUsed;
    }

    public void setUsed(Boolean used) {
        isUsed = used;
    }

    public Double getSpeed() {
        if (speed == null) return speed;
        return round(speed,2);
    }

    public void setSpeed(Double speed) {
        double speedRounded = round(speed, 2);
        if (speedRounded >= 0.01 && speedRounded <= 0.99)
        this.speed = speedRounded;
    }

    public Integer getCrewSize() {
        return crewSize;
    }

    public void setCrewSize(Integer crewSize) {
        if (crewSize >= 1 && crewSize <= 9999)
            this.crewSize = crewSize;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = round(rating,2);
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
