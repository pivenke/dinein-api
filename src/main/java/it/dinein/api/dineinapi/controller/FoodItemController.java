package it.dinein.api.dineinapi.controller;

import it.dinein.api.dineinapi.exception.ExceptionHandling;
import it.dinein.api.dineinapi.exception.FoodItemNotFoundByHotelierAndName;
import it.dinein.api.dineinapi.exception.ItemAlreadyExistException;
import it.dinein.api.dineinapi.exception.RestaurantNotFoundException;
import it.dinein.api.dineinapi.model.FoodItem;
import it.dinein.api.dineinapi.model.HttpResponse;
import it.dinein.api.dineinapi.service.FoodItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = {"/api/v1/hotelier/food-items"})
public class FoodItemController extends ExceptionHandling {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private FoodItemService foodItemService;

    @GetMapping("/list/{restaurantName}")
    public ResponseEntity<List<FoodItem>> getFoodItemsByRestaurantName(@PathVariable String restaurantName) {
        List<FoodItem> foodItems = foodItemService.getFoodItemsByRestaurantName(restaurantName);
        return new ResponseEntity<>(foodItems, HttpStatus.OK);
    }

    @GetMapping("/{name}")
    public ResponseEntity<FoodItem> findFoodItemByName(@PathVariable String name) {
        FoodItem item = foodItemService.findFoodItemByName(name);
        return new ResponseEntity<>(item, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<FoodItem> addFoodItem(@RequestParam("restaurantName") String restaurantName,
                                                @RequestParam("name") String name,
                                                @RequestParam("price") double price,
                                                @RequestParam(value = "img", required = false) MultipartFile img) throws ItemAlreadyExistException, RestaurantNotFoundException, IOException {
       FoodItem newItem = foodItemService.addNFoodItem(restaurantName,name,price,img);
        return new ResponseEntity<>(newItem, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<FoodItem> updateFoodItem(
                                                @RequestParam("currentName") String currentName,
                                                @RequestParam("restaurantName") String restaurantName,
                                                @RequestParam("name") String name,
                                                @RequestParam("price") double price,
                                                @RequestParam(value = "img", required = false) MultipartFile img) throws ItemAlreadyExistException, RestaurantNotFoundException, FoodItemNotFoundByHotelierAndName, IOException {
        FoodItem newItem = foodItemService.updateFoodItem(restaurantName,currentName,name,price,img);
        return new ResponseEntity<>(newItem, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public HttpEntity<HttpResponse> deleteFoodItem(
            @RequestParam(name = "restaurantName") String restaurantName,
            @RequestParam(name = "name") String name) throws FoodItemNotFoundByHotelierAndName {
        foodItemService.deleteFoodItem(restaurantName,name);
        return response(HttpStatus.OK, "ITEM DELETED SUCCESSFULLY");
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        HttpResponse body = new HttpResponse(httpStatus.value(),httpStatus,httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase());
        return new ResponseEntity<>(body, httpStatus);
    }
}
