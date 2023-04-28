package it.dinein.api.dineinapi.repository;

import it.dinein.api.dineinapi.model.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion,Long> {

    @Query("SELECT p FROM Promotion p WHERE p.hotelPromotion.restaurantName = :hotelName")
    List<Promotion> findByHotelName(@Param("hotelName") String hotelName);
}
