package com.example.techmoves.controller;

import com.example.techmoves.dto.OrderCreationDTO;
import com.example.techmoves.dto.OrderResponseDTO;
import com.example.techmoves.dto.OrderTakingDTO;
import com.example.techmoves.dto.StatusResponseDTO;
import com.example.techmoves.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderCreationDTO orderCreationDTO) {
        OrderResponseDTO response = orderService.createOrder(orderCreationDTO);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> takeOrder(@PathVariable Long id,
                                       @Valid @RequestBody OrderTakingDTO orderTakingDTO) {
        orderService.takeOrder(id);
        return ResponseEntity.ok(new StatusResponseDTO("SUCCESS"));
    }

    @GetMapping
    public ResponseEntity<?> listOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        List<OrderResponseDTO> orders = orderService.listOrders(page, limit);
        return ResponseEntity.ok(orders);
    }
}