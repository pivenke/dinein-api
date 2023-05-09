package it.dinein.api.dineinapi.repository;

import it.dinein.api.dineinapi.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Long> {
    @Query("SELECT AVG(r.rating) FROM Hotelier h JOIN h.reviews r WHERE h.restaurantName = :hotelName")
    Double findAverageRatingByHotelName(@Param("hotelName") String hotelName);
}
