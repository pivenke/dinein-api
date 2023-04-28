package it.dinein.api.dineinapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
public class Promotion implements Serializable {
    @Id // map p.key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;
    private String promoId;
    private String description;
    private String title;
    private double price;
    private String imageUrl;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    @JsonIgnore
    private Hotelier hotelPromotion;

    public Promotion() {
    }

    public Promotion(Long id, String promoId, String description, String title, double price, String imageUrl, Hotelier hotelPromotion) {
        this.id = id;
        this.promoId = promoId;
        this.description = description;
        this.title = title;
        this.price = price;
        this.imageUrl = imageUrl;
        this.hotelPromotion = hotelPromotion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPromoId() {
        return promoId;
    }

    public void setPromoId(String promoId) {
        this.promoId = promoId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Hotelier getHotelPromotion() {
        return hotelPromotion;
    }

    public void setHotelPromotion(Hotelier hotelPromotion) {
        this.hotelPromotion = hotelPromotion;
    }

    public static String generatePromotionId() {
        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();
        return "PROM-" + randomUUIDString.substring(0, 8);
    }
}
