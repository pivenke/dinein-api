package it.dinein.api.dineinapi.controller;

import it.dinein.api.dineinapi.exception.ExceptionHandling;
import it.dinein.api.dineinapi.model.HttpResponse;
import it.dinein.api.dineinapi.model.OrderItem;
import it.dinein.api.dineinapi.model.UserOrder;
import it.dinein.api.dineinapi.service.UserOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = {"/api/v1/hotelier/orders"})
public class UserOrderController extends ExceptionHandling {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private UserOrderService userOrderService;

    @GetMapping("/{restaurantName}")
    public ResponseEntity<List<UserOrder>> getOrdersByRestaurantName(@PathVariable String restaurantName) {
        List<UserOrder> userOrders = userOrderService.getAllByRestaurantName(restaurantName);
        return new ResponseEntity<>(userOrders, HttpStatus.OK);
    }

    @GetMapping("/{username}")
    public ResponseEntity<List<UserOrder>> getOrdersByUsername(@PathVariable String username) {
        List<UserOrder> userOrders = userOrderService.getAllByUser(username);
        return new ResponseEntity<>(userOrders, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserOrder>> getAllOrders() {
        List<UserOrder> userOrders = userOrderService.getAllOrders();
        return new ResponseEntity<>(userOrders, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<UserOrder> createOrder(@RequestParam(name = "description") String description,
                                 @RequestParam(name = "totalPrice") double totalPrice,
                                 @RequestParam(name = "restaurantName") String restaurantName,
                                 @RequestParam(name = "username") String username,
                                 @RequestBody List<OrderItem> orderItems)
    {
        UserOrder newOrder = userOrderService.createOrder(description,totalPrice,restaurantName,username,orderItems);
        return new ResponseEntity<>(newOrder, HttpStatus.OK);
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<UserOrder> updateOrder(@PathVariable(name = "orderId") Long orderId,
                                                 @RequestBody List<OrderItem> orderItems)
    {
        UserOrder updatedOrder = userOrderService.updateOrder(orderId,orderItems);
        return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
    }

    @PutMapping("/details/{orderId}")
    public ResponseEntity<UserOrder> updateOrderDetails(@PathVariable(name = "orderId") Long orderId,
                                                 @RequestBody UserOrder userOrder)
    {
        UserOrder updatedOrder = userOrderService.updateOrderDetails(orderId,userOrder);
        return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<UserOrder> removeOrderItem(@PathVariable(name = "orderId") Long orderId,
                                                     @RequestParam(name = "itemId") Long itemId)
    {
        UserOrder updatedOrder = userOrderService.removeOrderItem(orderId,itemId);
        return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
    }

    @PostMapping("/{orderId}")
    public ResponseEntity<UserOrder> addOrderItem(@PathVariable(name = "orderId") Long orderId,
                                                     @RequestBody OrderItem orderItem)
    {
        UserOrder updatedOrder = userOrderService.addOrderItem(orderId,orderItem);
        return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public HttpEntity<HttpResponse> deleteOrder(@RequestParam(name = "orderId") Long orderId)
    {
        userOrderService.deleteOrder(orderId);
        return response(HttpStatus.OK, "ITEM DELETED SUCCESSFULLY");
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        HttpResponse body = new HttpResponse(httpStatus.value(),httpStatus,httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase());
        return new ResponseEntity<>(body, httpStatus);
    }
}
