package it.dinein.api.dineinapi.repository;

import it.dinein.api.dineinapi.model.OrderItem;
import it.dinein.api.dineinapi.model.UserOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserOrderRepository extends JpaRepository<UserOrder,Long> {

    @Query("SELECT o FROM UserOrder o WHERE o.hotelOrder.restaurantName = :hotelName")
    List<UserOrder> findByHotelName(@Param("hotelName") String hotelName);

    @Query("SELECT o FROM UserOrder o WHERE o.user.username = :username")
    List<UserOrder> findByUsername(@Param("username") String username);
    @Query(value = "SELECT o.* FROM user_order o INNER JOIN user u ON o.user_id = u.id WHERE u.username = :username ORDER BY o.date DESC LIMIT 1", nativeQuery = true)
    UserOrder findLatestOrderByUsername(@Param("username") String username);

}
