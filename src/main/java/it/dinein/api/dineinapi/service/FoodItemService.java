package it.dinein.api.dineinapi.service;

import it.dinein.api.dineinapi.exception.FoodItemNotFoundByHotelierAndName;
import it.dinein.api.dineinapi.exception.ItemAlreadyExistException;
import it.dinein.api.dineinapi.exception.RestaurantNotFoundException;
import it.dinein.api.dineinapi.model.FoodItem;
import it.dinein.api.dineinapi.model.Hotelier;
import it.dinein.api.dineinapi.repository.FoodItemRepository;
import it.dinein.api.dineinapi.repository.HotelierRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

import static it.dinein.api.dineinapi.common.constant.ErrorUtil.*;

@Service
@Transactional
public class FoodItemService{
    private FoodItemRepository foodItemRepository;
    private StorageService storageService;
    private IHotelierService hotelierService;
    private HotelierRepository hotelierRepository;

    @Autowired
    public FoodItemService(FoodItemRepository foodItemRepository, StorageService storageService,IHotelierService hotelierService,HotelierRepository hotelierRepository) {
        this.hotelierService = hotelierService;
        this.foodItemRepository = foodItemRepository;
        this.storageService = storageService;
        this.hotelierRepository = hotelierRepository;
    }

    public List<FoodItem> getFoodItemsByRestaurantName(String restaurantName) {
        Hotelier hotelier = hotelierService.findHotelierByRestaurantName(restaurantName);
        return hotelier.getMenu();
    }

    public FoodItem findFoodItemByName(String name) {
        return foodItemRepository.findFoodItemByName(name);
    }

    public FoodItem addNFoodItem(String restaurantName, String name, double price, MultipartFile img) throws RestaurantNotFoundException, ItemAlreadyExistException, IOException {
        Hotelier hotelier = hotelierService.findHotelierByRestaurantName(restaurantName);
        if (hotelier != null)
        {
            FoodItem item = foodItemRepository.findByRestaurantNameAndFoodItemName(restaurantName,name);
            if (item != null)
            {
                throw new ItemAlreadyExistException(ITEM_ALREADY_EXIST);
            }
            else
            {
                FoodItem newItem = new FoodItem();
                newItem.setName(name);
                newItem.setItemId(generateUserId());
                newItem.setPrice(price);
                if (img != null && !img.isEmpty())
                {
                    String url = storageService.uploadFoodItemImage(restaurantName,name,img);
                    newItem.setImageUrl(url);
                }
                newItem.setHotelItem(hotelier);
                foodItemRepository.save(newItem);
                hotelier.getMenu().add(newItem);
                hotelierRepository.save(hotelier);

                return newItem;
            }
        }
        else
        {
            throw new RestaurantNotFoundException(RESTAURANT_NOT_FOUND);
        }
    }

    public FoodItem updateFoodItem(String restaurantName, String currentName, String name, double price, MultipartFile img) throws FoodItemNotFoundByHotelierAndName, IOException {
        FoodItem item = foodItemRepository.findByRestaurantNameAndFoodItemName(restaurantName,currentName);
        if (item != null)
        {
            item.setName(name);
            item.setPrice(price);
            if (img != null && !img.isEmpty())
            {
                storageService.deleteFoodImageFile(restaurantName,currentName);
                String url = storageService.uploadFoodItemImage(restaurantName,name,img);
                item.setImageUrl(url);
            }
            return foodItemRepository.save(item);
        }
        else
        {
            throw new FoodItemNotFoundByHotelierAndName(ITEM_NOT_FOUND_BY_NAME_HOTEL);
        }
    }

    public void deleteFoodItem(String restaurantName, String name) throws FoodItemNotFoundByHotelierAndName {
        FoodItem item = foodItemRepository.findByRestaurantNameAndFoodItemName(restaurantName,name);
        if (item != null)
        {
            storageService.deleteFoodImageFile(restaurantName,name);
            foodItemRepository.delete(item);
        }
        else
        {
            throw new FoodItemNotFoundByHotelierAndName(ITEM_NOT_FOUND_BY_NAME_HOTEL);
        }
    }

    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }
}
