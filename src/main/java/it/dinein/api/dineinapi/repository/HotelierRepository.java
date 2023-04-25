package it.dinein.api.dineinapi.repository;

import it.dinein.api.dineinapi.model.Hotelier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelierRepository extends JpaRepository<Hotelier,Long> {
    Hotelier findHotelierByRestaurantName(String restaurantName);

    Hotelier findHotelierByEmail(String email);
}
