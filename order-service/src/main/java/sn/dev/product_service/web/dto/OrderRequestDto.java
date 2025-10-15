package sn.dev.product_service.web.dto;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import sn.dev.product_service.data.entities.Order;
import sn.dev.product_service.data.entities.OrderItem;

@Getter
@Setter
public class OrderRequestDto {
    @NotBlank(message = "userId cannot be blank")
    private String userId;
    @NotBlank(message = "Order payment method cannot be blank")
    private String paymentMethod;
    @NotBlank(message = "Status cannot be blank")
    private String status;
    @NotNull(message = "At least one item is required")
    private List<OrderItemRequestDto> items; // produits commandés
}

