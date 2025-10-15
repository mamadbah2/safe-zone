package sn.dev.product_service.web.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sn.dev.product_service.client.product.ProductClient;
import sn.dev.product_service.data.entities.OrderItem;
import sn.dev.product_service.web.dto.OrderItemRequestDto;
import sn.dev.product_service.web.dto.ProductResponseDto;

@RequiredArgsConstructor
@Component
public class OrdersItemsMappers {
    private final ProductClient productClient;

    public OrderItem toEntity(OrderItemRequestDto orderItemRequestDto) {
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(orderItemRequestDto.getQuantity());

        var productId = orderItemRequestDto.getProductId();
        orderItem.setProductId(productId);
        ProductResponseDto productResponseDto = productClient.getById(productId);
        orderItem.setUnitPrice(productResponseDto.getPrice());

        return orderItem;
    }
}
