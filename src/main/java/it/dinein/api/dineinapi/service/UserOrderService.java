package it.dinein.api.dineinapi.service;

import it.dinein.api.dineinapi.model.Hotelier;
import it.dinein.api.dineinapi.model.OrderItem;
import it.dinein.api.dineinapi.model.User;
import it.dinein.api.dineinapi.model.UserOrder;
import it.dinein.api.dineinapi.repository.OrderItemRepository;
import it.dinein.api.dineinapi.repository.UserOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class UserOrderService {

    @Autowired
    private UserOrderRepository userOrderRepository;
    @Autowired
    private IUserService userService;
    @Autowired
    private IHotelierService hotelierService;
    @Autowired
    private OrderItemRepository orderItemRepository;

    public UserOrder createOrder(String description, double totalPrice, String restaurantName, String username, List<OrderItem> orderItems) {
        User user = userService.findUserByUserName(username);
        Hotelier hotelier = hotelierService.findHotelierByRestaurantName(restaurantName);
        UserOrder newOrder = new UserOrder();
        newOrder.setDate(new Date());
        newOrder.setDescription(description);
        newOrder.setPrice(totalPrice);
        newOrder.setCompleted(false);
        newOrder.setStatus("IN-PROGRESS");
        newOrder.setHotelOrder(hotelier);
        newOrder.setUser(user);
        // Save the order to the database
        UserOrder savedUserOrder = userOrderRepository.save(newOrder);

        // Set the order reference for the order items
        List<OrderItem> savedItems = new ArrayList<>();
        for (OrderItem item : orderItems) {
            item.setOrder(savedUserOrder);
            savedItems.add(orderItemRepository.save(item));
        }
        savedUserOrder.setItems(savedItems);
        return userOrderRepository.save(savedUserOrder);
    }

    public List<UserOrder> getAllOrders() {
        return userOrderRepository.findAll();
    }

    public List<UserOrder> getAllByRestaurantName(String restaurantName) {
        return userOrderRepository.findByHotelName(restaurantName);
    }

    public List<UserOrder> getAllByUser(String username) {
        return userOrderRepository.findByUsername(username);
    }

    public UserOrder updateOrder(Long orderId, List<OrderItem> newItems) {
        // Retrieve the user order by ID
        UserOrder userOrder = userOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order ID: " + orderId));

        // Retrieve the existing order items for the user order
        List<OrderItem> existingItems = userOrder.getItems();

        // Update the existing order items with the new values
        for (int i = 0; i < existingItems.size(); i++) {
            OrderItem existingItem = existingItems.get(i);
            OrderItem newItem = newItems.get(i);
            existingItem.setItemName(newItem.getItemName());
            existingItem.setQuantity(newItem.getQuantity());
            existingItem.setTotalPrice(newItem.getTotalPrice());
        }

        // Save the updated order items to the database
        existingItems = orderItemRepository.saveAll(existingItems);

        // Update the user order with the new order items
        userOrder.setItems(existingItems);

        // Save the updated user order to the database
        return userOrderRepository.save(userOrder);
    }

    public UserOrder updateOrderDetails(Long orderId, UserOrder order) {
        // Retrieve the user order by ID
        UserOrder userOrder = userOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order ID: " + orderId));

        if (userOrder != null) {
            userOrder.setDescription(order.getDescription());
            userOrder.setPrice(order.getPrice());
            userOrder.setStatus(order.getStatus());
            userOrder.setCompleted(order.isCompleted());

            // Replace the existing items list with the new items list
            List<OrderItem> newItems = order.getItems();
            userOrder.getItems().clear();
            for (OrderItem item : newItems) {
                item.setOrder(userOrder);
                userOrder.getItems().add(item);
            }

            // Save the updated user order to the database
            return userOrderRepository.save(userOrder);
        } else {
            throw new IllegalArgumentException("Invalid order ID: " + orderId);
        }
    }


    public UserOrder removeOrderItem(Long orderId, Long itemId) {
        UserOrder order = userOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order ID: " + orderId));

        OrderItem itemToRemove = null;

        for (OrderItem item : order.getItems()) {
            if (item.getId().equals(itemId)) {
                itemToRemove = item;
                break;
            }
        }

        if (itemToRemove != null) {
            order.getItems().remove(itemToRemove);
            UserOrder updatedOrder = userOrderRepository.save(order);
            orderItemRepository.delete(itemToRemove);
            return updatedOrder;
        } else {
            throw new IllegalArgumentException("Invalid order ID: " + orderId);
        }
    }

    public UserOrder addOrderItem(Long orderId, OrderItem item) {
        UserOrder order = userOrderRepository.findById(orderId)
                .orElseThrow(() ->  new IllegalArgumentException("Invalid order ID: " + orderId));

        item.setOrder(order);
        order.getItems().add(item);
        return userOrderRepository.save(order);
    }

    public void deleteOrder(Long orderId) {
        userOrderRepository.deleteById(orderId);
    }

    public String getOrderKeywords(String username) {
        StringBuilder keyword = new StringBuilder();
        UserOrder userOrder = userOrderRepository.findLatestOrderByUsername(username);
        for (OrderItem item: userOrder.getItems())
        {
            keyword.append(item.getItemName()).append(",");
        }
        return keyword.toString();
    }

    public Map<String, Integer> getItemQuantitySummary(String hotelName) {
        Map<String, Integer> itemSummary = new HashMap<>();

        List<UserOrder> orders = userOrderRepository.findByHotelName(hotelName);

        for (UserOrder order : orders) {
            for (OrderItem orderItem : order.getItems()) {
                String itemName = orderItem.getItemName();
                int itemQuantity = orderItem.getQuantity();

                if (itemName != null) {
                    if (itemSummary.containsKey(itemName)) {
                        int currentQuantity = itemSummary.get(itemName);
                        itemSummary.put(itemName, currentQuantity + itemQuantity);
                    } else {
                        itemSummary.put(itemName, itemQuantity);
                    }
                }
            }
        }

        return itemSummary;
    }

}
