package it.dinein.api.dineinapi.controller;

import it.dinein.api.dineinapi.exception.ExceptionHandling;
import it.dinein.api.dineinapi.exception.UserNotFoundException;
import it.dinein.api.dineinapi.model.HttpResponse;
import it.dinein.api.dineinapi.model.Reservation;
import it.dinein.api.dineinapi.service.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = {"/api/v1/hotelier/reservations"})
public class ReservationController extends ExceptionHandling {

    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private ReservationService reservationService;

    @GetMapping("/{restaurantName}")
    public ResponseEntity<List<Reservation>> getReservationsByRestaurantName(@PathVariable String restaurantName) {
        List<Reservation> reservations = reservationService.getReservationsByHotelName(restaurantName);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @GetMapping("/{username}")
    public ResponseEntity<List<Reservation>> findReservationsByUsername(@PathVariable String username) {
        List<Reservation> reservations = reservationService.getReservationsByUserName(username);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @PutMapping("/create")
    public ResponseEntity<Reservation> createReservation(
            @RequestParam(name = "hotelName") String hotelName,
            @RequestParam(name = "username") String username,
            @RequestBody Reservation reservation) throws UserNotFoundException {
        Reservation newReservation = reservationService.createReservation(username,hotelName,reservation);
        return new ResponseEntity<>(newReservation, HttpStatus.OK);
    }

    @PutMapping("/{reservationId}")
    public ResponseEntity<Reservation> updatePromotion(
            @PathVariable(name = "reservationId") Long reservationId,
            @RequestBody Reservation reservation)
    {
        Reservation updatedReservation = reservationService.updateReservation(reservationId,reservation);
        return new ResponseEntity<>(updatedReservation, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public HttpEntity<HttpResponse> deletePromotion(@RequestParam(name = "reservationId") Long reservationId)
    {
        reservationService.deleteReservation(reservationId);
        return response(HttpStatus.OK, "ITEM DELETED SUCCESSFULLY");
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        HttpResponse body = new HttpResponse(httpStatus.value(),httpStatus,httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase());
        return new ResponseEntity<>(body, httpStatus);
    }
}
