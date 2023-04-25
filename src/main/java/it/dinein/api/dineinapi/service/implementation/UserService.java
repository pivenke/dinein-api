package it.dinein.api.dineinapi.service.implementation;

import it.dinein.api.dineinapi.common.enumeration.Role;
import it.dinein.api.dineinapi.exception.*;
import it.dinein.api.dineinapi.model.User;
import it.dinein.api.dineinapi.model.UserPrincipal;
import it.dinein.api.dineinapi.repository.UserRepository;
import it.dinein.api.dineinapi.service.*;
import org.springframework.stereotype.Service;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static it.dinein.api.dineinapi.common.constant.UserImplementation.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.*;

@Service
@Transactional
@Qualifier("userDetailService")
public class UserService implements IUserService, UserDetailsService{
    private Logger LOGGER = LoggerFactory.getLogger(getClass()); //UserServiceImplementation.class
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder;
    private LoginAttemptService loginAttemptService;
    private EmailService emailService;
    private StorageService storageService;
    private ResetCodeService resetCodeService;

    @Autowired
    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       LoginAttemptService loginAttemptService,
                       EmailService emailService, StorageService storageService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
        this.emailService = emailService;
        this.storageService = storageService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findUserByUserName(username);
        if (user == null) {
            LOGGER.error(NOT_FOUND_FOR_THE_USERNAME + username);
            throw new UsernameNotFoundException(NOT_FOUND_FOR_THE_USERNAME + username);
        } else {
            validateLoginAttempt(user);
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            LOGGER.info(USER_SUCCESSFULLY_FOUND_BY_THE_USERNAME + username);
            return userPrincipal;
        }
    }

    private void validateLoginAttempt(User user) {
        if (user.isNotLocked()) {
            if (loginAttemptService.hasExceededMaxAttempt(user.getUsername())) {
                user.setNotLocked(false);
            } else {
                user.setNotLocked(true);
            }
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }

    @Override
    public User register(String firstName, String lastName, String username, String email, String password,String city,
                         String state, String country, String phone, String address, Date birthDate,Date anniversaryDate, boolean isMarried,Date wifeBirthDate) throws UserNotFoundException, EmailExistException, UsernameExistException, MessagingException {
        validateNewUsernameAndEmail(StringUtils.EMPTY, username, email);
        User user = new User();
        user.setUserId(generateUserId());
        String encodedPassword = encodePassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setCity(city);
        user.setState(state);
        user.setCountry(country);
        user.setPhone(phone);
        user.setAddress(address);
        user.setBirthDate(birthDate);
        user.setAnniversaryDate(anniversaryDate);
        user.setMarried(isMarried);
        user.setWifeBirthDate(wifeBirthDate);
        user.setEmail(email);
        user.setJoinedDate(new Date());
        user.setPassword(encodedPassword);
        user.setActive(true);
        user.setNotLocked(true);
        user.setRole(Role.ROLE_USER.name());
        user.setAuthorities(Role.ROLE_USER.getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
        userRepository.save(user);
        return user;
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }

    private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) throws UserNotFoundException, UsernameExistException, EmailExistException {
        User userByUsername = findUserByUserName(newUsername);
        User userByUEmail = findUserByEmail(newEmail);
        if (StringUtils.isNotBlank(currentUsername)) {
            User currentUser = findUserByUserName(currentUsername);
            if (currentUser == null) {
                throw new UserNotFoundException(USER_WAS_NOT_FIND_BY_USERNAME + currentUsername);
            }
            if (userByUsername != null && !currentUser.getId().equals(userByUsername.getId())) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByUEmail != null && !currentUser.getId().equals(userByUEmail.getId())) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser;
        } else {
            if (userByUsername != null) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByUEmail != null) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return null;
        }
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserByUserName(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public User addNewUser(String firstName, String lastName, String username, String email, String role, boolean isNotLocked, boolean isActive, MultipartFile profileImage
            , String country, String state, String city,String phone, String address, Date birthDate,Date anniversaryDate, boolean isMarried,Date wifeBirthDate) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException {
        validateNewUsernameAndEmail(StringUtils.EMPTY, username, email);
        User user = new User();
        String password = generatePassword();
        String encodedPassword = encodePassword(password);
        user.setUserId(generateUserId());
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setJoinedDate(new Date());
        user.setUsername(username);
        user.setEmail(email);
        user.setCountry(country);
        user.setCity(city);
        user.setState(state);
        user.setPhone(phone);
        user.setAddress(address);
        user.setBirthDate(birthDate);
        user.setAnniversaryDate(anniversaryDate);
        user.setMarried(isMarried);
        user.setWifeBirthDate(wifeBirthDate);
        user.setPassword(encodedPassword);
        user.setActive(isActive);
        user.setNotLocked(isNotLocked);
        user.setRole(getRoleEnumName(role).name());
        user.setAuthorities(getRoleEnumName(role).getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
        userRepository.save(user);
        saveProfileImage(user, profileImage);
        return user;
    }

    @Override
    public User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail, String role,String city, String state, boolean isNotLocked, boolean isActive, MultipartFile profileImage, String country, String phone,
                           String address, Date birthDate,Date anniversaryDate, boolean isMarried,Date wifeBirthDate) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException {
        User currentUser = validateNewUsernameAndEmail(currentUsername, newUsername, newEmail);
        currentUser.setFirstName(newFirstName);
        currentUser.setLastName(newLastName);
        currentUser.setUsername(newUsername);
        currentUser.setEmail(newEmail);
        currentUser.setCity(city);
        currentUser.setState(state);
        currentUser.setCountry(country);
        currentUser.setPhone(phone);
        currentUser.setAddress(address);
        currentUser.setBirthDate(birthDate);
        currentUser.setAnniversaryDate(anniversaryDate);
        currentUser.setMarried(isMarried);
        currentUser.setWifeBirthDate(wifeBirthDate);
        currentUser.setActive(isActive);
        currentUser.setNotLocked(isNotLocked);
        currentUser.setRole(getRoleEnumName(role).name());
        currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());
        userRepository.save(currentUser);
        saveProfileImage(currentUser, profileImage);
        return currentUser;
    }

    @Override
    public User updateUserProfileDetails(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail, String role,String city,String state, boolean isNotLocked, boolean isActive, String country, String phone,
                                         String address, Date birthDate,Date anniversaryDate, boolean isMarried,Date wifeBirthDate) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException {
        User currentUser = validateNewUsernameAndEmail(currentUsername, newUsername, newEmail);
        currentUser.setFirstName(newFirstName);
        currentUser.setLastName(newLastName);
        currentUser.setUsername(newUsername);
        currentUser.setCity(city);
        currentUser.setState(state);
        currentUser.setEmail(newEmail);
        currentUser.setCountry(country);
        currentUser.setPhone(phone);
        currentUser.setAddress(address);
        currentUser.setBirthDate(birthDate);
        currentUser.setAnniversaryDate(anniversaryDate);
        currentUser.setMarried(isMarried);
        currentUser.setWifeBirthDate(wifeBirthDate);
        currentUser.setActive(isActive);
        currentUser.setNotLocked(isNotLocked);
        currentUser.setRole(getRoleEnumName(role).name());
        currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());
        userRepository.save(currentUser);
        storageService.deleteFile(currentUsername);
        return currentUser;
    }

    @Override
    public void deleteUser(String username) {
        User user = userRepository.findUserByUsername(username);
        userRepository.deleteById(user.getId());
    }

    @Override
    public void resetPassword(String email) throws EmailNotFoundException, MessagingException {
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL + email);
        } else {
            String password = generatePassword();
            user.setPassword(encodePassword(password));
            userRepository.save(user);
            emailService.sendNewPasswordEmail(user.getFirstName(), password, email);
        }
    }

    @Override
    public User updateProfileImage(String username, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException {
        if (!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF).contains(profileImage.getContentType())) {
            throw new NotAnImageFileException(BAD_REQUEST, profileImage.getOriginalFilename() + " is not an valid image file.");
        }
        User user = validateNewUsernameAndEmail(username, null, null);
        saveProfileImage(user, profileImage);
        return user;
    }

    @Override
    public User timeBasedPasswordReset(String username, String code, String password) throws ResetCodeExpiredException {
        User user = userRepository.findUserByUsername(username);
        resetCodeService.verify(code);
        user.setPassword(encodePassword(password));
        return userRepository.save(user);
    }

    private void saveProfileImage(User user, MultipartFile profileImage) throws IOException, NotAnImageFileException {
        if (profileImage != null) {
            String url = getUploadedUrl(user.getUsername(),profileImage);
            user.setProfileImageUrl(url);
            userRepository.save(user);
        } else {
            user.setProfileImageUrl(getTemporaryProfileImageUrl(user.getUsername()));
            userRepository.save(user);
        }
    }

    private String getUploadedUrl(String username,MultipartFile multipartFile) throws IOException {
        return storageService.uploadMyProfileImage(username,multipartFile);
    }

    private String setProfileImageUrl(String username) {
        return storageService.getFileUrl(username);
    }

    private String getTemporaryProfileImageUrl(String username) {
        return storageService.getDefaultFileUrl();
    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
    }

}
