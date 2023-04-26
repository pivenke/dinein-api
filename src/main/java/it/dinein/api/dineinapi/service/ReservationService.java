package it.dinein.api.dineinapi.service;

import it.dinein.api.dineinapi.exception.UserNotFoundException;
import it.dinein.api.dineinapi.model.Hotelier;
import it.dinein.api.dineinapi.model.Reservation;
import it.dinein.api.dineinapi.model.User;
import it.dinein.api.dineinapi.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;

import static it.dinein.api.dineinapi.common.constant.UserImplementation.USER_WAS_NOT_FIND_BY_USERNAME;

@Service
@Transactional
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private IUserService userService;

    @Autowired
    private IHotelierService hotelierService;

    public List<Reservation> getReservationsByUserName(String username) {
        return reservationRepository.findReservationsByUsername(username);
    }

    public List<Reservation> getReservationsByHotelName(String hotelName) {
        return reservationRepository.findReservationsByHotelName(hotelName);
    }

    public Reservation createReservation(String username, String restaurantName, Reservation reservation) throws UserNotFoundException {
        // Load user by username
        User user = userService.findUserByUserName(username);

        // Load hotelier by restaurantName
        Hotelier hotelier = hotelierService.findHotelierByRestaurantName(restaurantName);

        if (user != null)
        {
            if (hotelier != null)
            {
                // Set the user and hotelier for the reservation
                reservation.setUser(user);
                reservation.setHotelReservation(hotelier);

                // Save the reservation to the database
                return reservationRepository.save(reservation);
            }
            else
            {
                throw new UserNotFoundException(USER_WAS_NOT_FIND_BY_USERNAME + restaurantName);
            }
        }
        else
        {
            throw new UserNotFoundException(USER_WAS_NOT_FIND_BY_USERNAME + username);
        }
    }

    public Reservation updateReservation(Long id, Reservation reservationDetails) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation id: " + id));

        reservation.setCheckInDate(reservationDetails.getCheckInDate());
        reservation.setPaxCount(reservationDetails.getPaxCount());
        reservation.setFinished(reservationDetails.isFinished());
        reservation.setStatus(reservationDetails.getStatus());
        reservation.setPrice(reservationDetails.getPrice());

        return reservationRepository.save(reservation);
    }

    public void deleteReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation id: " + id));

        reservationRepository.delete(reservation);
    }
}

