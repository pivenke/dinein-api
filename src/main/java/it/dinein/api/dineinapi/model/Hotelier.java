package it.dinein.api.dineinapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
public class Hotelier implements Serializable {
    // declaring user properties
    @Id // map p.key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long id;
    private String hotelierId;
    private String restaurantName;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String email;
    private String city;
    private String state;
    private String phone;
    private String address;
    private String openAt;
    private String closeAt;
    private int tableCount;
    private Double rating;
    private String imageUrl;
    private Date lastLoginDate;
    private Date lastLoginDateDisplay;
    private Date joinedDate;
    private String role;
    private String[] authorities;
    private boolean isActive;
    private boolean isNotLocked;
    @OneToMany(mappedBy = "hotelItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<FoodItem> menu;
    @OneToMany(mappedBy = "hotelReservation", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Reservation> reservations;
    @OneToMany(mappedBy = "hotelOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<UserOrder> userOrders;
    @OneToMany(mappedBy = "hotelPromotion", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Promotion> promotions;
    @OneToMany(mappedBy = "hotelReview", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Review> reviews;
    @OneToMany(mappedBy = "hotelTable", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Table> tables;

    public Hotelier() {
    }

    public Hotelier(Long id, String hotelierId, String restaurantName, String password, String email, String city,
                    String state, String phone, String address, String openAt, String closeAt, int tableCount, Double rating,String imageUrl,
                    Date lastLoginDate, Date lastLoginDateDisplay, Date joinedDate, String role, String[] authorities,
                    boolean isActive, boolean isNotLocked, List<FoodItem> menu, List<Reservation> reservations,
                    List<UserOrder> userOrders, List<Promotion> promotions, List<Review> reviews, List<Table> tables) {
        this.id = id;
        this.hotelierId = hotelierId;
        this.restaurantName = restaurantName;
        this.password = password;
        this.email = email;
        this.city = city;
        this.state = state;
        this.phone = phone;
        this.address = address;
        this.openAt = openAt;
        this.closeAt = closeAt;
        this.tableCount = tableCount;
        this.rating = rating;
        this.imageUrl = imageUrl;
        this.lastLoginDate = lastLoginDate;
        this.lastLoginDateDisplay = lastLoginDateDisplay;
        this.joinedDate = joinedDate;
        this.role = role;
        this.authorities = authorities;
        this.isActive = isActive;
        this.isNotLocked = isNotLocked;
        this.menu = menu;
        this.reservations = reservations;
        this.userOrders = userOrders;
        this.promotions = promotions;
        this.reviews = reviews;
        this.tables = tables;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHotelierId() {
        return hotelierId;
    }

    public void setHotelierId(String hotelierId) {
        this.hotelierId = hotelierId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOpenAt() {
        return openAt;
    }

    public void setOpenAt(String openAt) {
        this.openAt = openAt;
    }

    public String getCloseAt() {
        return closeAt;
    }

    public void setCloseAt(String closeAt) {
        this.closeAt = closeAt;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public Date getLastLoginDateDisplay() {
        return lastLoginDateDisplay;
    }

    public void setLastLoginDateDisplay(Date lastLoginDateDisplay) {
        this.lastLoginDateDisplay = lastLoginDateDisplay;
    }

    public Date getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(Date joinedDate) {
        this.joinedDate = joinedDate;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String[] getAuthorities() {
        return authorities;
    }

    public void setAuthorities(String[] authorities) {
        this.authorities = authorities;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isNotLocked() {
        return isNotLocked;
    }

    public void setNotLocked(boolean notLocked) {
        isNotLocked = notLocked;
    }

    public List<FoodItem> getMenu() {
        return menu;
    }

    public void setMenu(List<FoodItem> menu) {
        this.menu = menu;
    }

    public List<UserOrder> getUserOrders() {
        return userOrders;
    }

    public void setUserOrders(List<UserOrder> userOrders) {
        this.userOrders = userOrders;
    }

    public List<Promotion> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<Promotion> promotions) {
        this.promotions = promotions;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public List<UserOrder> getOrders() {
        return userOrders;
    }

    public void setOrders(List<UserOrder> userOrders) {
        this.userOrders = userOrders;
    }

    public int getTableCount() {
        return tableCount;
    }

    public List<Table> getTables() {
        return tables;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public void setTableCount(int tableCount) {
        this.tableCount = tableCount;
    }
}
