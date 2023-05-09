package it.dinein.api.dineinapi.service;

import it.dinein.api.dineinapi.exception.RestaurantNotFoundException;
import it.dinein.api.dineinapi.model.Hotelier;
import it.dinein.api.dineinapi.model.Promotion;
import it.dinein.api.dineinapi.repository.PromotionRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PromotionService {

    private PromotionRepository promotionRepository;
    private IHotelierService hotelierService;
    private StorageService storageService;

    @Autowired
    public PromotionService(PromotionRepository promotionRepository, IHotelierService hotelierService, StorageService storageService) {
        this.promotionRepository = promotionRepository;
        this.hotelierService = hotelierService;
        this.storageService = storageService;
    }

    public List<Promotion> getPromotionsByHotelName(String hotelName) {
        return promotionRepository.findByHotelName(hotelName);
    }

    public Promotion createPromotion(String hotelName, String description, String title, double price, MultipartFile img) throws RestaurantNotFoundException, IOException {
        Hotelier hotelier = hotelierService.findHotelierByRestaurantName(hotelName);
        if (hotelier != null) {
            Promotion promotion = new Promotion();
            promotion.setDescription(description);
            String promoId = generatePromoId();
            promotion.setPromoId(promoId);
            promotion.setTitle(title);
            promotion.setPrice(price);
            promotion.setHotelPromotion(hotelier);
            if (img != null && !img.isEmpty())
            {
                String url = storageService.uploadPromoImage(hotelName,promoId,img);
                promotion.setImageUrl(url);
            }
            return promotionRepository.save(promotion);
        } else {
            throw new RestaurantNotFoundException("Hotel not found with the name: " + hotelName);
        }
    }

    public Promotion updatePromotion(String hotelName, Long promotionId, Promotion promotion,MultipartFile img) throws RestaurantNotFoundException, IOException {
        Optional<Promotion> existingPromotion = promotionRepository.findById(promotionId);
        if (existingPromotion.isPresent()) {
            Optional<Hotelier> hotelier = Optional.ofNullable(hotelierService.findHotelierByRestaurantName(hotelName));
            if (hotelier.isPresent()) {
                Promotion updatedPromotion = existingPromotion.get();
                updatedPromotion.setTitle(promotion.getTitle());
                updatedPromotion.setDescription(promotion.getDescription());
                updatedPromotion.setPrice(promotion.getPrice());
                updatedPromotion.setHotelPromotion(hotelier.get());
                if (img != null && !img.isEmpty())
                {
                    storageService.deletePromoImageFile(hotelName,updatedPromotion.getPromoId());
                    String url = storageService.uploadPromoImage(hotelName,updatedPromotion.getPromoId(),img);
                    promotion.setImageUrl(url);
                }
                return promotionRepository.save(updatedPromotion);
            }
            else
            {
                throw new RestaurantNotFoundException("Hotel not found with name: " + hotelName);
            }
        } else {
            throw new EntityNotFoundException("Promotion not found with id: " + promotionId);
        }
    }

    public void deletePromotion(Long promotionId) {
        Optional<Promotion> existingPromotion = promotionRepository.findById(promotionId);
        if (existingPromotion.isPresent()) {
            storageService.deletePromoImageFile(existingPromotion.get().getHotelPromotion().getRestaurantName(),existingPromotion.get().getPromoId());
            promotionRepository.deleteById(promotionId);
        } else {
            throw new EntityNotFoundException("Promotion not found with id: " + promotionId);
        }
    }

    private String generatePromoId() {
        return RandomStringUtils.randomNumeric(10);
    }
}

