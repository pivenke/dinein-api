package it.dinein.api.dineinapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class Reservation implements Serializable {
    @Id // map p.key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;
    private Long tableId;
    private String reservationId;
    private Date checkInDate;
    private int paxCount;
    private boolean isFinished;
    private String status;
    private double price;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Hotelier hotelReservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User user;

    public Reservation() {
    }

    public Reservation(Long id, Long tableId, String reservationId, Date checkInDate, int paxCount, boolean isFinished, String status, double price, Hotelier hotelReservation, User user) {
        this.id = id;
        this.tableId = tableId;
        this.reservationId = reservationId;
        this.checkInDate = checkInDate;
        this.paxCount = paxCount;
        this.isFinished = isFinished;
        this.status = status;
        this.price = price;
        this.hotelReservation = hotelReservation;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public Date getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(Date checkInDate) {
        this.checkInDate = checkInDate;
    }

    public Hotelier getHotelReservation() {
        return hotelReservation;
    }

    public void setHotelReservation(Hotelier hotelReservation) {
        this.hotelReservation = hotelReservation;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getPaxCount() {
        return paxCount;
    }

    public void setPaxCount(int paxCount) {
        this.paxCount = paxCount;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public String getStatus() {
        return status;
    }

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
