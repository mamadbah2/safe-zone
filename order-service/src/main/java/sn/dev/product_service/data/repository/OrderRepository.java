package sn.dev.product_service.data.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import sn.dev.product_service.data.entities.Order;

public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByUserId(String userId);
    void deleteByUserId(String userId);
    
}
