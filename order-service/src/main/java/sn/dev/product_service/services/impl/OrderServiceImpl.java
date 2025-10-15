package sn.dev.product_service.services.impl;

import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import sn.dev.product_service.data.entities.Order;
import sn.dev.product_service.data.entities.OrderItem;
import sn.dev.product_service.data.repository.OrderRepository;
import sn.dev.product_service.services.OrderService;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;


    @Override
    public Order create(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    @Override
    public Order getById(String id) {
        return orderRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found with id: " + id));
    }

    @Override
    public List<Order> getByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public Order update(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public void delete(Order order) {
        orderRepository.delete(order);
    }

    @Override
    public Double computeOrdersItems(List<OrderItem> orderItemList) {
        if (orderItemList == null) return 0.;
        return orderItemList.stream().mapToDouble(orderItem -> {
            return orderItem.getQuantity() * orderItem.getUnitPrice();
        }).sum();
    }

    @Override
    public void deleteByUserId(String userId) {
        orderRepository.deleteByUserId(userId);
    }
}
