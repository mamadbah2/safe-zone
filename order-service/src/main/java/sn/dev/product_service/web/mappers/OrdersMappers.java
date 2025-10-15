package sn.dev.product_service.web.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sn.dev.product_service.data.entities.Order;
import sn.dev.product_service.data.entities.OrderItem;
import sn.dev.product_service.web.dto.OrderItemResponseDto;
import sn.dev.product_service.web.dto.OrderRequestDto;
import sn.dev.product_service.web.dto.OrderResponseDto;

import java.util.List;

@RequiredArgsConstructor
@Component
public class OrdersMappers {

    private final OrdersItemsMappers ordersItemsMappers;

    public Order toEntity(OrderRequestDto orderRequestDto) {
        Order order = new Order();

        order.setUserId(orderRequestDto.getUserId());
        order.setStatus("pending");
        order.setPaymentMethod(orderRequestDto.getPaymentMethod());

        List<OrderItem> orderItemList = orderRequestDto.getItems().stream().map(ordersItemsMappers::toEntity).toList();
        order.setOrderItemList(orderItemList);
        Double totalPrice = orderItemList.stream().mapToDouble(orderItem -> {
            return orderItem.getQuantity() * orderItem.getUnitPrice();
        }).sum();
        order.setTotal(totalPrice);

        return order;
    }

    public OrderResponseDto toResponse(Order order) {
        OrderResponseDto orderResponseDto = new OrderResponseDto();
        orderResponseDto.setId(order.getId());
        orderResponseDto.setUserId(order.getUserId());
        orderResponseDto.setStatus( order.getStatus());
        orderResponseDto.setPaymentMethod(order.getPaymentMethod());
        orderResponseDto.setCreatedAt(order.getCreatedAt());
        orderResponseDto.setItems(order.getOrderItemList().stream().map(
                OrderItemResponseDto::new
        ).toList());

        return orderResponseDto;
    }
}
