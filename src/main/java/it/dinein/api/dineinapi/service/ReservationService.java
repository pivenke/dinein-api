package it.dinein.api.dineinapi.service;

import it.dinein.api.dineinapi.exception.EmailNotFoundException;
import it.dinein.api.dineinapi.exception.RestaurantNotFoundException;
import it.dinein.api.dineinapi.exception.TabletNotFoundException;
import it.dinein.api.dineinapi.exception.UserNotFoundException;
import it.dinein.api.dineinapi.model.Hotelier;
import it.dinein.api.dineinapi.model.Reservation;
import it.dinein.api.dineinapi.model.Table;
import it.dinein.api.dineinapi.model.User;
import it.dinein.api.dineinapi.repository.ReservationRepository;
import it.dinein.api.dineinapi.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
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
    private TableService tableService;
    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private IHotelierService hotelierService;
    @Autowired
    private MailService mailService;

    public List<Reservation> getReservationsByUserName(String username) {
        return reservationRepository.findReservationsByUsername(username);
    }

    public List<Reservation> getReservationsByHotelName(String hotelName) {
        return reservationRepository.findReservationsByHotelName(hotelName);
    }

    public Reservation createReservation(String username, String restaurantName, Reservation reservation) throws UserNotFoundException, TabletNotFoundException, EmailNotFoundException {
        // Load user by username
        User user = userService.findUserByUserName(username);

        // Load hotelier by restaurantName
        Hotelier hotelier = hotelierService.findHotelierByRestaurantName(restaurantName);

        if (user != null)
        {
            if (hotelier != null)
            {
                // set reservation table
                Table reserved = tableService.getTableById(reservation.getTableId());

                if (reserved.isAvailabilityStatus())
                {
                    reserved.setAvailabilityStatus(false);
                    tableRepository.save(reserved);
                    // Set the user and hotelier for the reservation
                    reservation.setUser(user);
                    reservation.setHotelReservation(hotelier);

                    // Save the reservation to the database
                    Reservation saved = reservationRepository.save(reservation);
                    // Send the email with the Reservation data
                    try {
                        mailService.sendEmailWithTemplate(user.getEmail(), "Reservation Confirmation!", "reservation-template", saved, hotelier);
                        return saved;
                    } catch (MessagingException e) {
                        System.out.println("Reservation Placed Successfully. Failed to send Email");
                        return saved;
                    }
                }
                else
                {
                    throw new TabletNotFoundException("The table is already occupied!");
                }
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

    public Reservation updateReservation(Long id, Reservation reservationDetails) throws TabletNotFoundException {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation id: " + id));

        if (reservationDetails.getTableId() != null)
        {
            Table exist = tableService.getTableById(reservation.getTableId());
            exist.setAvailabilityStatus(true);
            tableRepository.save(exist);

            Table newReserved = tableService.getTableById(reservationDetails.getTableId());
            newReserved.setAvailabilityStatus(false);
            tableRepository.save(newReserved);
        }

        reservation.setCheckInDate(reservationDetails.getCheckInDate());
        reservation.setPaxCount(reservationDetails.getPaxCount());
        reservation.setFinished(reservationDetails.isFinished());
        reservation.setStatus(reservationDetails.getStatus());
        reservation.setPrice(reservationDetails.getPrice());

        return reservationRepository.save(reservation);
    }

    public void deleteReservation(Long id) throws TabletNotFoundException {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation id: " + id));
        Table reserved = tableService.getTableById(reservation.getTableId());
        reserved.setAvailabilityStatus(true);
        tableRepository.save(reserved);
        reservationRepository.delete(reservation);
    }
}

