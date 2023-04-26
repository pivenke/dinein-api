package it.dinein.api.dineinapi.repository;

import it.dinein.api.dineinapi.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("SELECT r FROM Reservation r WHERE r.user.username = :username")
    List<Reservation> findReservationsByUsername(@Param("username") String username);

    @Query("SELECT r FROM Reservation r WHERE r.hotelReservation.restaurantName = :hotelName")
    List<Reservation> findReservationsByHotelName(@Param("hotelName") String hotelName);
}
