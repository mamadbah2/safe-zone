package sn.dev.product_service.client.product;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import sn.dev.product_service.config.FeignSupportConfig;
import sn.dev.product_service.web.dto.ProductResponseDto;

@FeignClient(
        name = "product-service",
        url = "${product.service.url}",
        configuration = FeignSupportConfig.class
)
public interface ProductClient {
    @GetMapping("/{id}")
    ProductResponseDto getById(@PathVariable("id") String id);
}
