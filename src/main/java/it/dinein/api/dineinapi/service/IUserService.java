package it.dinein.api.dineinapi.service;

import it.dinein.api.dineinapi.exception.*;
import it.dinein.api.dineinapi.model.Hotelier;
import it.dinein.api.dineinapi.model.User;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface IUserService {

    User register(String firstName, String lastName, String username, String email, String password, String city,
                  String state, String country, String phone, String address, Date birthDate,Date anniversaryDate, boolean isMarried,Date wifeBirthDate) throws UserNotFoundException, EmailExistException, UsernameExistException, MessagingException;

    List<User> getUsers();

    User findUserByUserName(String username);

    User findUserByEmail(String email);

    User addNewUser(String firstName,
                    String lastName, String username, String email, String role,
                    boolean isNotLocked, boolean isActive, MultipartFile profileImage, String country, String state,String city,String phone,
                    String address, Date birthDate,Date anniversaryDate, boolean isMarried,Date wifeBirthDate) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException;

    User updateUser(String currentUsername, String newFirstName,
                    String newLastName, String newUsername, String newEmail, String role,String city, String state,
                    boolean isNotLocked, boolean isActive, MultipartFile profileImage, String country, String phone,
                    String address, Date birthDate,Date anniversaryDate, boolean isMarried, Date wifeBirthDate) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException;

    User updateUserProfileDetails(String currentUsername, String newFirstName,
                                  String newLastName, String newUsername, String newEmail, String role,
                                  String city, String state, boolean isNotLocked, boolean isActive, String country, String phone, String address,
                                  Date birthDate,Date anniversaryDate, boolean isMarried,Date wifeBirthDate) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException;

    void deleteUser(String username) throws IOException;

    void resetPassword(String email) throws EmailNotFoundException, MessagingException;

    User updateProfileImage(String username, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException;

    User timeBasedPasswordReset(String username, String code, String password) throws ResetCodeExpiredException;
    List<Hotelier> searchHoteliers(String city, String state, Double rating);
}