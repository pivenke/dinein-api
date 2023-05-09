package it.dinein.api.dineinapi.controller;

import it.dinein.api.dineinapi.exception.*;
import it.dinein.api.dineinapi.model.*;
import it.dinein.api.dineinapi.service.*;
import it.dinein.api.dineinapi.utility.UJWTTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
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
@RequestMapping(value = {"/api/v1/user"})
public class UserController extends ExceptionHandling{
    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    public static final String EMAIL_SENT = "New password was sent to the email: ";
    public static final String DELETED_SUCCESSFULLY = "User deleted successfully";
    private IUserService userService;

    private AuthenticationManager authenticationManager;
    private UJWTTokenProvider ujwtTokenProvider;
    private ResetCodeService resetCodeService;
    @Autowired
    private UserOrderService userOrderService;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private TableService tableService;


    @Autowired
    public UserController(IUserService userService, AuthenticationManager authenticationManager, UJWTTokenProvider ujwtTokenProvider,ResetCodeService resetCodeService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.ujwtTokenProvider = ujwtTokenProvider;
        this.resetCodeService = resetCodeService;
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user){
        authenticate(user.getUsername(), user.getPassword());
        User loginUser = userService.findUserByUserName(user.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeaders(userPrincipal);
        return new ResponseEntity<>(loginUser, jwtHeader, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) throws UserNotFoundException, EmailExistException, UsernameExistException, MessagingException {
        User newUser = userService.register(user.getFirstName(),user.getLastName(),user.getUsername(),user.getEmail(),user.getPassword(),user.getCity(),user.getState(),user.getCountry(),
                user.getPhone(),user.getAddress(),user.getBirthDate(),user.getAnniversaryDate(),user.isMarried(),user.getWifeBirthDate());
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<User> addUser(@RequestParam("firstName") String firstName,
                                            @RequestParam("lastName") String lastName,
                                            @RequestParam("username") String username,
                                            @RequestParam("email") String email,
                                            @RequestParam("role") String role,
                                            @RequestParam("country") String country,
                                            @RequestParam("city") String city,
                                            @RequestParam("state") String state,
                                            @RequestParam("phone") String phone,
                                            @RequestParam("birthDate") Date birthDate,
                                            @RequestParam("anniversaryDate") Date anniversaryDate,
                                            @RequestParam("address") String address,
                                            @RequestParam("isMarried") String isMarried,
                                            @RequestParam("wifeBirthDate") Date wifeBirthDate,
                                            @RequestParam("isActive") String isActive,
                                            @RequestParam("isNotLocked") String isNotLocked,
                                            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws UserNotFoundException, EmailExistException, IOException, UsernameExistException, NotAnImageFileException, NotAnImageFileException, NotAnImageFileException {

        User newUser = userService.addNewUser(firstName,lastName,username,email,role,Boolean.parseBoolean(isNotLocked),Boolean.parseBoolean(isActive),profileImage,country,state,city,phone,address,
                birthDate,anniversaryDate,Boolean.getBoolean(isMarried),wifeBirthDate);
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<User> updateUser(@RequestParam("currentUsername") String currentUsername,
                                           @RequestParam("firstName") String firstName,
                                           @RequestParam("lastName") String lastName,
                                           @RequestParam("username") String username,
                                           @RequestParam("email") String email,
                                           @RequestParam("role") String role,
                                           @RequestParam("country") String country,
                                           @RequestParam("city") String city,
                                           @RequestParam("state") String state,
                                           @RequestParam("phone") String phone,
                                           @RequestParam("birthDate") Date birthDate,
                                           @RequestParam("anniversaryDate") Date anniversaryDate,
                                           @RequestParam("address") String address,
                                           @RequestParam("isMarried") String isMarried,
                                           @RequestParam("wifeBirthDate") Date wifeBirthDate,
                                           @RequestParam("isActive") String isActive,
                                           @RequestParam("isNotLocked") String isNotLocked,
                                           @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws UserNotFoundException, EmailExistException, IOException, UsernameExistException, NotAnImageFileException {

        User updatedUser = userService.updateUser(currentUsername,firstName,lastName,username,email,role,city,state,Boolean.parseBoolean(isNotLocked),Boolean.parseBoolean(isActive),profileImage,country,
                phone,address,birthDate,anniversaryDate,Boolean.getBoolean(isMarried),wifeBirthDate);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @PostMapping("/update/profileData")
    public ResponseEntity<User> updateUser(@RequestParam("currentUsername") String currentUsername,
                                           @RequestParam("firstName") String firstName,
                                           @RequestParam("lastName") String lastName,
                                           @RequestParam("username") String username,
                                           @RequestParam("email") String email,
                                           @RequestParam("role") String role,
                                           @RequestParam("country") String country,
                                           @RequestParam("city") String city,
                                           @RequestParam("state") String state,
                                           @RequestParam("phone") String phone,
                                           @RequestParam("birthDate") Date birthDate,
                                           @RequestParam("anniversaryDate") Date anniversaryDate,
                                           @RequestParam("address") String address,
                                           @RequestParam("isMarried") String isMarried,
                                           @RequestParam("wifeBirthDate") Date wifeBirthDate,
                                           @RequestParam("isActive") String isActive,
                                           @RequestParam("isNotLocked") String isNotLocked) throws UserNotFoundException, EmailExistException, IOException, UsernameExistException, NotAnImageFileException {

        User updatedUser = userService.updateUserProfileDetails(currentUsername,firstName,lastName,username,email,role,city,state,Boolean.parseBoolean(isNotLocked),
                Boolean.parseBoolean(isActive),country,phone,address,birthDate,anniversaryDate,Boolean.getBoolean(isMarried),wifeBirthDate);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<User> getUser(@PathVariable("username") String username){
        User user = userService.findUserByUserName(username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users = userService.getUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }


    @GetMapping("/resetPassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) throws EmailNotFoundException, MessagingException {
        userService.resetPassword(email);
        return response(HttpStatus.OK, EMAIL_SENT + email);
    }

    @DeleteMapping("/delete/{username}")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("username") String username) throws IOException {
        userService.deleteUser(username);
        return response(HttpStatus.OK, DELETED_SUCCESSFULLY);
    }

    @PostMapping("/updateProfileImage")
    public ResponseEntity<User> updateProfileImage(@RequestParam("username") String username, @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws UserNotFoundException, EmailExistException, IOException, UsernameExistException, NotAnImageFileException {
        User user = userService.updateProfileImage(username, profileImage);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/password/reset/{username}")
    public ResponseEntity<User> resetByTime(@PathVariable("username") String username, @RequestParam("code") String code, @RequestParam("password") String password) throws ResetCodeExpiredException {
        User user = userService.timeBasedPasswordReset(username,code,password);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/password/code/{username}")
    public ResponseEntity<HttpResponse> getCode(@PathVariable("username") String username, @RequestParam("email") String email) throws MessagingException {
        resetCodeService.generateCode(username,email);
        return response(HttpStatus.OK, "Code sent to the email: " + email);
    }

    @GetMapping("/orders/{username}")
    public ResponseEntity<List<UserOrder>> getOrdersByUsername(@PathVariable String username) {
        List<UserOrder> userOrders = userOrderService.getAllByUser(username);
        return new ResponseEntity<>(userOrders, HttpStatus.OK);
    }

    @PostMapping("/orders/create")
    public ResponseEntity<UserOrder> createOrder(@RequestParam(name = "description") String description,
                                                 @RequestParam(name = "totalPrice") double totalPrice,
                                                 @RequestParam(name = "restaurantName") String restaurantName,
                                                 @RequestParam(name = "username") String username,
                                                 @RequestBody List<OrderItem> orderItems)
    {
        UserOrder newOrder = userOrderService.createOrder(description,totalPrice,restaurantName,username,orderItems);
        return new ResponseEntity<>(newOrder, HttpStatus.OK);
    }

    @PutMapping("/orders/{orderId}")
    public ResponseEntity<UserOrder> updateOrder(@PathVariable(name = "orderId") Long orderId,
                                                 @RequestBody List<OrderItem> orderItems)
    {
        UserOrder updatedOrder = userOrderService.updateOrder(orderId,orderItems);
        return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
    }

    @PutMapping("/orders/details/{orderId}")
    public ResponseEntity<UserOrder> updateOrderDetails(@PathVariable(name = "orderId") Long orderId,
                                                        @RequestBody UserOrder userOrder)
    {
        UserOrder updatedOrder = userOrderService.updateOrderDetails(orderId,userOrder);
        return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
    }

    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<UserOrder> removeOrderItem(@PathVariable(name = "orderId") Long orderId,
                                                     @RequestParam(name = "itemId") Long itemId)
    {
        UserOrder updatedOrder = userOrderService.removeOrderItem(orderId,itemId);
        return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
    }

    @PostMapping("/orders/{orderId}")
    public ResponseEntity<UserOrder> addOrderItem(@PathVariable(name = "orderId") Long orderId,
                                                  @RequestBody OrderItem orderItem)
    {
        UserOrder updatedOrder = userOrderService.addOrderItem(orderId,orderItem);
        return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
    }

    @DeleteMapping("/orders/delete")
    public HttpEntity<HttpResponse> deleteOrder(@RequestParam(name = "orderId") Long orderId)
    {
        userOrderService.deleteOrder(orderId);
        return response(HttpStatus.OK, "ITEM DELETED SUCCESSFULLY");
    }

    @GetMapping("/reservations/{username}")
    public ResponseEntity<List<Reservation>> findReservationsByUsername(@PathVariable String username) {
        List<Reservation> reservations = reservationService.getReservationsByUserName(username);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @PostMapping("/reservations/create")
    public ResponseEntity<Reservation> createReservation(
            @RequestParam(name = "hotelName") String hotelName,
            @RequestParam(name = "username") String username,
            @RequestBody Reservation reservation) throws UserNotFoundException, TabletNotFoundException, EmailNotFoundException {
        Reservation newReservation = reservationService.createReservation(username,hotelName,reservation);
        return new ResponseEntity<>(newReservation, HttpStatus.OK);
    }

    @PutMapping("/reservations/{reservationId}")
    public ResponseEntity<Reservation> updateReservation(
            @PathVariable(name = "reservationId") Long reservationId,
            @RequestBody Reservation reservation) throws TabletNotFoundException {
        Reservation updatedReservation = reservationService.updateReservation(reservationId,reservation);
        return new ResponseEntity<>(updatedReservation, HttpStatus.OK);
    }

    @DeleteMapping("/reservations/delete")
    public HttpEntity<HttpResponse> deletePromotion(@RequestParam(name = "reservationId") Long reservationId) throws TabletNotFoundException {
        reservationService.deleteReservation(reservationId);
        return response(HttpStatus.OK, "ITEM DELETED SUCCESSFULLY");
    }

    @PostMapping("/reviews/create")
    public HttpEntity<Review> addReview(@RequestParam(name = "hotelName") String hotelName,@RequestParam(name = "username") String username,
                                        @RequestParam(name = "comment") String comment,@RequestParam(name = "rating") Double rating) throws UserNotFoundException, RestaurantNotFoundException {
        Review review = reviewService.addReviewByHotelNameAndUserName(hotelName,username,comment,rating);
        return new ResponseEntity<>(review, HttpStatus.OK);
    }

    @GetMapping("/tables/available")
    public HttpEntity<List<Long>> getAvailableList(@RequestParam(name = "hotelName") String hotelName) throws UserNotFoundException, RestaurantNotFoundException {
        List<Long> availableList = tableService.getAvailableTables(hotelName);
        return new ResponseEntity<>(availableList, HttpStatus.OK);
    }

    @GetMapping("/order-history/{username}")
    public ResponseEntity<String> findOrderHistory(@PathVariable String username) {
        String historyKeyword = userOrderService.getOrderKeywords(username);
        return new ResponseEntity<>(historyKeyword, HttpStatus.OK);
    }

    @GetMapping("/hotels/filter")
    public ResponseEntity<List<Hotelier>> filter(@RequestParam("city") String city, @RequestParam("state") String state, @RequestParam("rating") Double rating) throws Exception {
        List<Hotelier> hoteliers = userService.searchHoteliers(city,state,rating);
        return new ResponseEntity<>(hoteliers, HttpStatus.OK);
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        HttpResponse body = new HttpResponse(httpStatus.value(),httpStatus,httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase());
        return new ResponseEntity<>(body, httpStatus);
    }

    private HttpHeaders getJwtHeaders(UserPrincipal userPrincipal) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWT_TOKEN_HEADER, ujwtTokenProvider.generateJwtToken(userPrincipal));
        return httpHeaders;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
