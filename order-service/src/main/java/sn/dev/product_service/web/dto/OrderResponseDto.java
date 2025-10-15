package sn.dev.product_service.web.dto;

import java.time.Instant;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import sn.dev.product_service.data.entities.Order;

@Data
@NoArgsConstructor
public class OrderResponseDto {
    private String id;
    private String userId;
    private Integer total;
    private String status;
    private String paymentMethod;
    private Instant createdAt;
    private List<OrderItemResponseDto> items;
}
