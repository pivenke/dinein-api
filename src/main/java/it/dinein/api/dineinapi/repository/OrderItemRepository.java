package it.dinein.api.dineinapi.repository;

import it.dinein.api.dineinapi.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
