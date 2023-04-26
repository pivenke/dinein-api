package it.dinein.api.dineinapi.controller;

import it.dinein.api.dineinapi.exception.ExceptionHandling;
import it.dinein.api.dineinapi.exception.RestaurantNotFoundException;
import it.dinein.api.dineinapi.model.HttpResponse;
import it.dinein.api.dineinapi.model.Promotion;
import it.dinein.api.dineinapi.service.PromotionService;
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
@RequestMapping(value = {"/api/v1/hotelier/promotions"})
public class PromotionController extends ExceptionHandling {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private PromotionService promotionService;

    @GetMapping("/{restaurantName}")
    public ResponseEntity<List<Promotion>> getPromotionsByRestaurantName(@PathVariable String restaurantName) {
        List<Promotion> promotions = promotionService.getPromotionsByHotelName(restaurantName);
        return new ResponseEntity<>(promotions, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<Promotion> createPromotion(
            @RequestParam(name = "hotelName") String hotelName,
            @RequestParam(name = "description") String description,
            @RequestParam(name = "title") String title,
            @RequestParam(name = "price") double price,
            @RequestParam(name = "img", required = false) MultipartFile img) throws RestaurantNotFoundException, IOException {
      Promotion newPromotion = promotionService.createPromotion(hotelName,description,title,price,img);
      return new ResponseEntity<>(newPromotion, HttpStatus.OK);
    }

    @PutMapping("/{promotionId}")
    public ResponseEntity<Promotion> updatePromotion(
            @RequestParam(name = "hotelName") String hotelName,
            @RequestParam(name = "img", required = false) MultipartFile img,
            @PathVariable(name = "promotionId") Long promotionId,
            @RequestBody Promotion promotion) throws RestaurantNotFoundException, IOException {
        Promotion updatedPromotion = promotionService.updatePromotion(hotelName,promotionId,promotion,img);
        return new ResponseEntity<>(updatedPromotion, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public HttpEntity<HttpResponse> deletePromotion(@RequestParam(name = "promotionId") Long promotionId)
    {
        promotionService.deletePromotion(promotionId);
        return response(HttpStatus.OK, "ITEM DELETED SUCCESSFULLY");
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        HttpResponse body = new HttpResponse(httpStatus.value(),httpStatus,httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase());
        return new ResponseEntity<>(body, httpStatus);
    }
}
