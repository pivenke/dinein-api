package it.dinein.api.dineinapi.service;

import it.dinein.api.dineinapi.exception.RestaurantNotFoundException;
import it.dinein.api.dineinapi.exception.UserNotFoundException;
import it.dinein.api.dineinapi.model.Hotelier;
import it.dinein.api.dineinapi.model.Review;
import it.dinein.api.dineinapi.model.User;
import it.dinein.api.dineinapi.repository.HotelierRepository;
import it.dinein.api.dineinapi.repository.ReviewRepository;
import it.dinein.api.dineinapi.service.implementation.HotelierService;
import it.dinein.api.dineinapi.service.implementation.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private HotelierService hotelierService;
    @Autowired
    private HotelierRepository hotelierRepository;

    public List<Review> getReviewsByHotelier(String hotelierName) throws RestaurantNotFoundException {
        Hotelier hotelier = hotelierService.findHotelierByRestaurantName(hotelierName);
        if (hotelier != null)
        {
            return hotelier.getReviews();
        }
        else
        {
            throw new RestaurantNotFoundException("Hotel not found with name: " + hotelierName);
        }
    }

    public Double getRating(String hotelierName) throws RestaurantNotFoundException {
        Hotelier hotelier = hotelierService.findHotelierByRestaurantName(hotelierName);
        if (hotelier != null)
        {
            return reviewRepository.findAverageRatingByHotelName(hotelierName);
        }
        else
        {
            throw new RestaurantNotFoundException("Hotel not found with name: " + hotelierName);
        }
    }

    public Review addReviewByHotelNameAndUserName(String hotelName, String userName, String comment, Double rating) throws RestaurantNotFoundException, UserNotFoundException {
        Hotelier hotel = hotelierService.findHotelierByRestaurantName(hotelName);
        if (hotel == null) {
            throw new RestaurantNotFoundException("Hotel not found with name: " + hotelName);
        }

        User user = userService.findUserByUserName(userName);
        if (user == null) {
            throw new UserNotFoundException("User not found with username: " + userName);
        }

        Review review = new Review();
        review.setDate(new Date());
        review.setComment(comment);
        review.setRating(rating);
        review.setUser(user);
        review.setHotelReview(hotel);
        Review saved = reviewRepository.save(review);
        hotel.setRating(getRating(hotelName));
        return saved;
    }
}
