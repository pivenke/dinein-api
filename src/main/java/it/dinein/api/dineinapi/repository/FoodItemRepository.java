package it.dinein.api.dineinapi.repository;

import it.dinein.api.dineinapi.model.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem,Long> {

    FoodItem findFoodItemByName(String name);
    @Query("SELECT f FROM FoodItem f JOIN f.hotelItem h WHERE h.restaurantName = :restaurantName AND f.name = :foodItemName")
    FoodItem findByRestaurantNameAndFoodItemName(@Param("restaurantName") String restaurantName, @Param("foodItemName") String foodItemName);

}
