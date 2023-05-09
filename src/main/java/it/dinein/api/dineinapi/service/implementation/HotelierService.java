package it.dinein.api.dineinapi.service.implementation;

import it.dinein.api.dineinapi.common.enumeration.Role;
import it.dinein.api.dineinapi.exception.*;
import it.dinein.api.dineinapi.model.Hotelier;
import it.dinein.api.dineinapi.model.HotelierPrincipal;
import it.dinein.api.dineinapi.repository.HotelierRepository;
import it.dinein.api.dineinapi.service.*;
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
import org.springframework.stereotype.Service;
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
@Qualifier("hotelierDetailService")
public class HotelierService implements IHotelierService, UserDetailsService {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    private HotelierRepository hotelierRepository;

    private BCryptPasswordEncoder passwordEncoder;
    private LoginAttemptService loginAttemptService;
    private EmailService emailService;
    private StorageService storageService;
    private ResetCodeService resetCodeService;

    @Autowired
    public HotelierService(HotelierRepository hotelierRepository,
                           BCryptPasswordEncoder passwordEncoder,
                           LoginAttemptService loginAttemptService,
                           EmailService emailService, StorageService storageService) {
        this.hotelierRepository = hotelierRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
        this.emailService = emailService;
        this.storageService = storageService;
    }

    @Override
    public UserDetails loadUserByUsername(String restaurantName) throws UsernameNotFoundException {
        Hotelier hotelier = findHotelierByRestaurantName(restaurantName);
        if (hotelier == null) {
            LOGGER.error(NOT_FOUND_FOR_THE_USERNAME + restaurantName);
            throw new UsernameNotFoundException(NOT_FOUND_FOR_THE_USERNAME + restaurantName);
        } else {
            validateLoginAttempt(hotelier);
            hotelier.setLastLoginDateDisplay(hotelier.getLastLoginDate());
            hotelier.setLastLoginDate(new Date());
            hotelierRepository.save(hotelier);
            HotelierPrincipal hotelierPrincipal = new HotelierPrincipal(hotelier);
            LOGGER.info(USER_SUCCESSFULLY_FOUND_BY_THE_USERNAME + restaurantName);
            return hotelierPrincipal;
        }
    }

    private void validateLoginAttempt(Hotelier hotelier) {
        if (hotelier.isNotLocked()) {
            if (loginAttemptService.hasExceededMaxAttempt(hotelier.getRestaurantName())) {
                hotelier.setNotLocked(false);
            } else {
                hotelier.setNotLocked(true);
            }
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(hotelier.getRestaurantName());
        }
    }

    @Override
    public Hotelier register(String restaurantName, String email, String password, String city, String state, String phone, String address, String openAt, String closeAt, int tableCount) throws UserNotFoundException, EmailExistException, UsernameExistException, MessagingException {
        validateNewUsernameAndEmail(StringUtils.EMPTY, restaurantName, email);
        Hotelier hotelier = new Hotelier();
        hotelier.setHotelierId(generateUserId());
        String encodedPassword = encodePassword(password);
        hotelier.setRestaurantName(restaurantName);
        hotelier.setCity(city);
        hotelier.setState(state);
        hotelier.setOpenAt(openAt);
        hotelier.setCloseAt(closeAt);
        hotelier.setTableCount(tableCount);
        hotelier.setPhone(phone);
        hotelier.setAddress(address);
        hotelier.setEmail(email);
        hotelier.setJoinedDate(new Date());
        hotelier.setPassword(encodedPassword);
        hotelier.setActive(true);
        hotelier.setNotLocked(true);
        hotelier.setRole(Role.ROLE_HOTELIER.name());
        hotelier.setAuthorities(Role.ROLE_HOTELIER.getAuthorities());
        hotelier.setImageUrl(getTemporaryProfileImageUrl(restaurantName));
        hotelierRepository.save(hotelier);
        return hotelier;
    }
    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }

    private Hotelier validateNewUsernameAndEmail(String currentRestaurantName, String newRestaurantName, String newEmail) throws UserNotFoundException, UsernameExistException, EmailExistException {
        Hotelier hotelierByUsername = findHotelierByRestaurantName(newRestaurantName);
        Hotelier hotelierByEmail = findHotelierByEmail(newEmail);
        if (StringUtils.isNotBlank(currentRestaurantName)) {
            Hotelier currentHotelier = findHotelierByRestaurantName(currentRestaurantName);
            if (currentHotelier == null) {
                throw new UserNotFoundException(USER_WAS_NOT_FIND_BY_USERNAME + currentRestaurantName);
            }
            if (hotelierByUsername != null && !currentHotelier.getId().equals(hotelierByUsername.getId())) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (hotelierByEmail != null && !currentHotelier.getId().equals(hotelierByEmail.getId())) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return currentHotelier;
        } else {
            if (hotelierByUsername != null) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (hotelierByEmail != null) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return null;
        }
    }

    @Override
    public List<Hotelier> getRestaurants() {
        return hotelierRepository.findAll();
    }

    @Override
    public Hotelier findHotelierByRestaurantName(String restaurantName) {
        return hotelierRepository.findHotelierByRestaurantName(restaurantName);
    }

    @Override
    public Hotelier findHotelierByEmail(String email) {
        return hotelierRepository.findHotelierByEmail(email);
    }

    @Override
    public Hotelier addNewHotelier(String restaurantName, String email,String city, String state, String phone, String address, String openAt, String closeAt, int tableCount, String role, boolean isNotLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException {
        validateNewUsernameAndEmail(StringUtils.EMPTY, restaurantName, email);
        Hotelier hotelier = new Hotelier();
        String password = generatePassword();
        String encodedPassword = encodePassword(password);
        hotelier.setHotelierId(generateUserId());
        hotelier.setJoinedDate(new Date());
        hotelier.setRestaurantName(restaurantName);
        hotelier.setState(state);
        hotelier.setOpenAt(openAt);
        hotelier.setCloseAt(closeAt);
        hotelier.setTableCount(tableCount);
        hotelier.setCity(city);
        hotelier.setEmail(email);
        hotelier.setPhone(phone);
        hotelier.setAddress(address);
        hotelier.setPassword(encodedPassword);
        hotelier.setActive(isActive);
        hotelier.setNotLocked(isNotLocked);
        hotelier.setRole(getRoleEnumName(role).name());
        hotelier.setAuthorities(getRoleEnumName(role).getAuthorities());
        hotelier.setImageUrl(getTemporaryProfileImageUrl(restaurantName));
        hotelierRepository.save(hotelier);
        saveProfileImage(hotelier, profileImage);
        return hotelier;
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    @Override
    public Hotelier updateHotelier(String currentRestaurantName, String restaurantName, String email, String city, String state, String phone, String address, String openAt, String closeAt, int tableCount, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException {
        Hotelier currentHotelier = validateNewUsernameAndEmail(currentRestaurantName, restaurantName, email);
        currentHotelier.setRestaurantName(restaurantName);
        currentHotelier.setEmail(email);
        currentHotelier.setCity(city);
        currentHotelier.setState(state);
        currentHotelier.setPhone(phone);
        currentHotelier.setOpenAt(openAt);
        currentHotelier.setCloseAt(closeAt);
        currentHotelier.setTableCount(tableCount);
        currentHotelier.setAddress(address);
        currentHotelier.setActive(isActive);
        hotelierRepository.save(currentHotelier);
        storageService.deleteFile(currentRestaurantName);
        saveProfileImage(currentHotelier, profileImage);
        return currentHotelier;
    }

    @Override
    public Hotelier updateHotelierProfileDetails(String currentRestaurantName, String restaurantName, String email, String city, String state, String phone, String address, String openAt, String closeAt, int tableCount, boolean isActive) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException {
        Hotelier currentHotelier = validateNewUsernameAndEmail(currentRestaurantName, restaurantName, email);
        currentHotelier.setRestaurantName(restaurantName);
        currentHotelier.setCity(city);
        currentHotelier.setState(state);
        currentHotelier.setEmail(email);
        currentHotelier.setOpenAt(openAt);
        currentHotelier.setCloseAt(closeAt);
        currentHotelier.setTableCount(tableCount);
        currentHotelier.setPhone(phone);
        currentHotelier.setAddress(address);
        currentHotelier.setActive(isActive);
        hotelierRepository.save(currentHotelier);
        return currentHotelier;
    }

    @Override
    public void deleteHotelier(String restaurantName) throws IOException {
        Hotelier hotelier = hotelierRepository.findHotelierByRestaurantName(restaurantName);
        hotelierRepository.deleteById(hotelier.getId());
    }

    @Override
    public void resetPassword(String email) throws EmailNotFoundException, MessagingException {
        Hotelier hotelier = hotelierRepository.findHotelierByEmail(email);
        if (hotelier == null) {
            throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL + email);
        } else {
            String password = generatePassword();
            hotelier.setPassword(encodePassword(password));
            hotelierRepository.save(hotelier);
            emailService.sendNewPasswordEmail(hotelier.getRestaurantName(), password, email);
        }
    }

    @Override
    public Hotelier updateProfileImage(String restaurantName, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException, RestaurantNotFoundException {
        if (!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF).contains(profileImage.getContentType())) {
            throw new NotAnImageFileException(BAD_REQUEST, profileImage.getOriginalFilename() + " is not an valid image file.");
        }
        Hotelier hotelier = findHotelierByRestaurantName(restaurantName);
        if (hotelier != null)
        {
            saveProfileImage(hotelier, profileImage);
            return hotelier;
        }
        else
        {
            throw new RestaurantNotFoundException("Restaurant was not found by " + restaurantName);
        }
    }

    @Override
    public Hotelier timeBasedPasswordReset(String restaurantName, String code, String password) throws ResetCodeExpiredException {
        Hotelier hotelier = hotelierRepository.findHotelierByRestaurantName(restaurantName);
        resetCodeService.verify(code);
        hotelier.setPassword(encodePassword(password));
        hotelier.setNotLocked(true);
        return hotelierRepository.save(hotelier);
    }

    private void saveProfileImage(Hotelier hotelier, MultipartFile profileImage) throws IOException, NotAnImageFileException {
        if (profileImage != null) {
            String url = getUploadedUrl(hotelier.getRestaurantName(),profileImage);
            hotelier.setImageUrl(url);
            hotelierRepository.save(hotelier);
        } else {
            hotelier.setImageUrl(getTemporaryProfileImageUrl(hotelier.getRestaurantName()));
            hotelierRepository.save(hotelier);
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
