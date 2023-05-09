package it.dinein.api.dineinapi.controller;

import it.dinein.api.dineinapi.exception.*;
import it.dinein.api.dineinapi.model.*;
import it.dinein.api.dineinapi.service.IHotelierService;
import it.dinein.api.dineinapi.service.ResetCodeService;
import it.dinein.api.dineinapi.service.ReviewService;
import it.dinein.api.dineinapi.service.TableService;
import it.dinein.api.dineinapi.utility.HJWTTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static it.dinein.api.dineinapi.common.constant.Security.JWT_TOKEN_HEADER;

@RestController
@RequestMapping(value = {"/api/v1/hotelier"})
public class HotelierController extends ExceptionHandling{
    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    public static final String EMAIL_SENT = "New password was sent to the email: ";
    public static final String DELETED_SUCCESSFULLY = "User deleted successfully";
    private IHotelierService hotelierService;

    private AuthenticationManager authenticationManager;
    private HJWTTokenProvider hjwtTokenProvider;
    private ResetCodeService resetCodeService;
    private ReviewService reviewService;
    private TableService tableService;


    @Autowired
    public HotelierController(IHotelierService hotelierService, AuthenticationManager authenticationManager,
                              HJWTTokenProvider hjwtTokenProvider, ResetCodeService resetCodeService,
                              ReviewService reviewService, TableService tableService) {
        this.hotelierService = hotelierService;
        this.authenticationManager = authenticationManager;
        this.hjwtTokenProvider = hjwtTokenProvider;
        this.resetCodeService = resetCodeService;
        this.reviewService = reviewService;
        this.tableService = tableService;
    }

    @PostMapping("/login")
    public ResponseEntity<Hotelier> login(@RequestBody Hotelier hotelier){
        authenticate(hotelier.getRestaurantName(), hotelier.getPassword());
        Hotelier loginUser = hotelierService.findHotelierByRestaurantName(hotelier.getRestaurantName());
        HotelierPrincipal hotelierPrincipal = new HotelierPrincipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeaders(hotelierPrincipal);
        return new ResponseEntity<>(loginUser, jwtHeader, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<Hotelier> register(@RequestBody Hotelier hotelier) throws UserNotFoundException, EmailExistException, UsernameExistException, MessagingException {
        Hotelier newHotelier = hotelierService.register(hotelier.getRestaurantName(),hotelier.getEmail(),hotelier.getPassword(),hotelier.getCity(),hotelier.getState(),
                hotelier.getPhone(),hotelier.getAddress(),hotelier.getOpenAt(),hotelier.getCloseAt(), hotelier.getTableCount());
        return new ResponseEntity<>(newHotelier, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Hotelier> addUser(@RequestParam("restaurantName") String restaurantName,
                                        @RequestParam("lastName") String lastName,
                                        @RequestParam("username") String username,
                                        @RequestParam("email") String email,
                                        @RequestParam("role") String role,
                                        @RequestParam("country") String country,
                                        @RequestParam("city") String city,
                                        @RequestParam("state") String state,
                                        @RequestParam("phone") String phone,
                                        @RequestParam("address") String address,
                                        @RequestParam("openAt") String openAt,
                                        @RequestParam("closeAt") String closeAt,
                                        @RequestParam("tableCount") int tableCount,
                                        @RequestParam("isActive") String isActive,
                                        @RequestParam("isNotLocked") String isNotLocked,
                                        @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws UserNotFoundException, EmailExistException, IOException, UsernameExistException, NotAnImageFileException, NotAnImageFileException, NotAnImageFileException {

        Hotelier newUser = hotelierService.addNewHotelier(restaurantName,email,city,state,phone,address,openAt,closeAt, tableCount, role,Boolean.parseBoolean(isNotLocked),
                Boolean.parseBoolean(isActive),profileImage);
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<Hotelier> updateUser(@RequestParam("currentRestaurantName") String currentRestaurantName,
                                               @RequestParam("restaurantName") String restaurantName,
                                               @RequestParam("email") String email,
                                               @RequestParam("city") String city,
                                               @RequestParam("state") String state,
                                               @RequestParam("phone") String phone,
                                               @RequestParam("address") String address,
                                               @RequestParam("openAt") String openAt,
                                               @RequestParam("closeAt") String closeAt,
                                               @RequestParam("tableCount") int tableCount,
                                               @RequestParam("isActive") String isActive,
                                               @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws UserNotFoundException, EmailExistException, IOException, UsernameExistException, NotAnImageFileException {

        Hotelier updatedUser = hotelierService.updateHotelier(currentRestaurantName,restaurantName,email,city,state,phone,address,openAt,closeAt,tableCount,
                Boolean.parseBoolean(isActive),profileImage);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @PostMapping("/update/profileData")
    public ResponseEntity<Hotelier> updateUser(@RequestParam("currentRestaurantName") String currentRestaurantName,
                                               @RequestParam("restaurantName") String restaurantName,
                                               @RequestParam("email") String email,
                                               @RequestParam("city") String city,
                                               @RequestParam("state") String state,
                                               @RequestParam("phone") String phone,
                                               @RequestParam("address") String address,
                                               @RequestParam("openAt") String openAt,
                                               @RequestParam("closeAt") String closeAt,
                                               @RequestParam("tableCount") int tableCount,
                                               @RequestParam("isActive") String isActive) throws UserNotFoundException, EmailExistException, IOException, UsernameExistException, NotAnImageFileException {

        Hotelier updatedUser = hotelierService.updateHotelierProfileDetails(currentRestaurantName,restaurantName,email,city,state,phone,address,openAt,closeAt,tableCount,Boolean.parseBoolean(isActive));
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<Hotelier> getUser(@PathVariable("username") String username){
        Hotelier user = hotelierService.findHotelierByRestaurantName(username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Hotelier>> getAllUsers(){
        List<Hotelier> users = hotelierService.getRestaurants();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }


    @GetMapping("/resetPassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) throws EmailNotFoundException, MessagingException {
        hotelierService.resetPassword(email);
        return response(HttpStatus.OK, EMAIL_SENT + email);
    }

    @DeleteMapping("/delete/{username}")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("username") String username) throws IOException {
        hotelierService.deleteHotelier(username);
        return response(HttpStatus.OK, DELETED_SUCCESSFULLY);
    }

    @PostMapping("/updateProfileImage")
    public ResponseEntity<Hotelier> updateProfileImage(@RequestParam("username") String username, @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws UserNotFoundException, EmailExistException, IOException, UsernameExistException, NotAnImageFileException, RestaurantNotFoundException {
        Hotelier user = hotelierService.updateProfileImage(username, profileImage);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/reviews")
    public ResponseEntity<List<Review>> getReviews(@RequestParam("hotelName") String hotelName) throws RestaurantNotFoundException {
        List<Review> reviews = reviewService.getReviewsByHotelier(hotelName);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @PostMapping("/tables/create")
    public ResponseEntity<Table> addTable(@RequestParam("hotelName") String hotelName) throws RestaurantNotFoundException {
        Table table = tableService.addHotelTable(hotelName);
        return new ResponseEntity<>(table, HttpStatus.OK);
    }

    @DeleteMapping("/tables/remove")
    public ResponseEntity<HttpResponse> removeTable(@RequestParam("tableId") Long tableId) throws Exception {
        tableService.removeTable(tableId);
        return response(HttpStatus.OK, "Table removed!");
    }

    @GetMapping("/tables/list")
    public ResponseEntity<List<Table>> getTables(@RequestParam("hotelName") String hotelName) throws Exception {
        List<Table>  tables = tableService.getTables(hotelName);
        return new ResponseEntity<>(tables, HttpStatus.OK);
    }

    @PutMapping("/password/reset/{username}")
    public ResponseEntity<Hotelier> resetByTime(@PathVariable("username") String username, @RequestParam("code") String code, @RequestParam("password") String password) throws ResetCodeExpiredException {
        Hotelier user = hotelierService.timeBasedPasswordReset(username,code,password);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/password/code/{username}")
    public ResponseEntity<HttpResponse> getCode(@PathVariable("username") String username, @RequestParam("email") String email) throws MessagingException {
        resetCodeService.generateCode(username,email);
        return response(HttpStatus.OK, "Code sent to the email: " + email);
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        HttpResponse body = new HttpResponse(httpStatus.value(),httpStatus,httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase());
        return new ResponseEntity<>(body, httpStatus);
    }

    private HttpHeaders getJwtHeaders(HotelierPrincipal hotelierPrincipal) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWT_TOKEN_HEADER, hjwtTokenProvider.generateJwtToken(hotelierPrincipal));
        return httpHeaders;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
