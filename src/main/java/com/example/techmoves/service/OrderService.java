package com.example.techmoves.service;


import com.example.techmoves.dto.OrderCreationDTO;
import com.example.techmoves.dto.OrderResponseDTO;
import com.example.techmoves.exception.OrderAlreadyTakenException;
import com.example.techmoves.exception.OrderNotFoundException;
import com.example.techmoves.model.Order;
import com.example.techmoves.model.OrderStatus;
import com.example.techmoves.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final DistanceService distanceService;

    @Value("${app.orders.page-size-default}")
    private int defaultPageSize;

    @Value("${app.orders.page-size-max}")
    private int maxPageSize;

    public OrderService(OrderRepository orderRepository, DistanceService distanceService) {
        this.orderRepository = orderRepository;
        this.distanceService = distanceService;
    }

    @Transactional
    public OrderResponseDTO createOrder(OrderCreationDTO orderCreationDto) {
        int distance = distanceService.calculateDistance(
                orderCreationDto.getOrigin(),
                orderCreationDto.getDestination()
        );

        Order order = new Order(distance, OrderStatus.UNASSIGNED);
        order = orderRepository.save(order);

        return new OrderResponseDTO(
                order.getId(),
                order.getDistance(),
                order.getStatus().name()
        );
    }

    @Transactional
    public void takeOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        if (order.getStatus() == OrderStatus.TAKEN) {
            throw new OrderAlreadyTakenException(id);
        }

        order.setStatus(OrderStatus.TAKEN);
        orderRepository.save(order);
    }

    public List<OrderResponseDTO> listOrders(int page, int limit) {
        validatePaginationParameters(page, limit);

        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        Page<Order> orders = orderRepository.findAll(pageRequest);

        return orders.stream()
                .map(order -> new OrderResponseDTO(
                        order.getId(),
                        order.getDistance(),
                        order.getStatus().name()))
                .collect(Collectors.toList());
    }

    private void validatePaginationParameters(int page, int limit) {
        if (page < 1) {
            throw new IllegalArgumentException("Page must be greater than 0");
        }
        if (limit < 1 || limit > maxPageSize) {
            throw new IllegalArgumentException("Limit must be between 1 and " + maxPageSize);
        }
    }
}