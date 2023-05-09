package it.dinein.api.dineinapi.repository;

import it.dinein.api.dineinapi.model.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TableRepository extends JpaRepository<Table,Long> {

    @Query("SELECT t.id FROM Table t WHERE t.hotelTable.restaurantName = :hotelName AND t.availabilityStatus = true")
    List<Long> findAvailableTableIdsByHotelName(@Param("hotelName") String hotelName);

    @Query("SELECT t FROM Table t WHERE t.hotelTable.restaurantName = :hotelName")
    List<Table> findTableIdsByHotelName(@Param("hotelName") String hotelName);

}
