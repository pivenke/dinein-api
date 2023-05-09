package it.dinein.api.dineinapi.service;

import it.dinein.api.dineinapi.exception.*;
import it.dinein.api.dineinapi.model.Hotelier;
import it.dinein.api.dineinapi.model.User;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public interface IHotelierService {
    Hotelier register(String restaurantName, String email, String password, String city,
                      String state, String phone, String address, String openAt, String closeAt, int tableCount) throws UserNotFoundException, EmailExistException, UsernameExistException, MessagingException;

    List<Hotelier> getRestaurants();

    Hotelier findHotelierByRestaurantName(String restaurantName);

    Hotelier findHotelierByEmail(String email);

    Hotelier addNewHotelier(String restaurantName, String email, String city,
                        String state, String phone, String address, String openAt, String closeAt, int tableCount, String role,
                    boolean isNotLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException;

    Hotelier updateHotelier(String currentRestaurantName, String restaurantName, String email, String city,
                        String state, String phone, String address, String openAt, String closeAt, int tableCount,
                     boolean isActive, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException;

    Hotelier updateHotelierProfileDetails(String currentRestaurantName, String restaurantName, String email, String city,
                                      String state, String phone, String address, String openAt, String closeAt, int tableCount, boolean isActive) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException;

    void deleteHotelier(String restaurantName) throws IOException;

    void resetPassword(String email) throws EmailNotFoundException, MessagingException;

    Hotelier updateProfileImage(String restaurantName, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException, RestaurantNotFoundException;

    Hotelier timeBasedPasswordReset(String restaurantName, String code, String password) throws ResetCodeExpiredException;
}
