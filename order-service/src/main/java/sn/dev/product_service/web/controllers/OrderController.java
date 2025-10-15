package sn.dev.product_service.web.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import sn.dev.product_service.web.dto.OrderRequestDto;
import sn.dev.product_service.web.dto.OrderResponseDto;

@RequestMapping("/api/orders")
public interface OrderController {
    @PostMapping
    ResponseEntity<OrderResponseDto> create(@ModelAttribute @Valid OrderRequestDto orderRequestDto);

    @GetMapping
    ResponseEntity<List<OrderResponseDto>> getAll();

    @GetMapping("/{id}")
    ResponseEntity<OrderResponseDto> getById(@PathVariable String id);

    @PutMapping("/{id}")
    ResponseEntity<OrderResponseDto> update(@ModelAttribute @Valid OrderRequestDto orderRequestDto,
                                            @PathVariable String id);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable String id);
}
