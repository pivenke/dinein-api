package it.dinein.api.dineinapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@javax.persistence.Table(name = "dining_table")
public class Table implements Serializable {
    @Id // map p.key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;
    boolean availabilityStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    @JsonIgnore
    private Hotelier hotelTable;

    public Table(Long id, boolean availabilityStatus, Hotelier hotelTable) {
        this.id = id;
        this.availabilityStatus = availabilityStatus;
        this.hotelTable = hotelTable;
    }

    public Table() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(boolean availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public Hotelier getHotelTable() {
        return hotelTable;
    }

    public void setHotelTable(Hotelier hotelTable) {
        this.hotelTable = hotelTable;
    }
}
