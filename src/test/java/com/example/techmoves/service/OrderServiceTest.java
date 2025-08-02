package com.example.techmoves.service;

import com.example.techmoves.dto.OrderCreationDTO;
import com.example.techmoves.dto.OrderResponseDTO;
import com.example.techmoves.exception.OrderAlreadyTakenException;
import com.example.techmoves.model.Order;
import com.example.techmoves.model.OrderStatus;
import com.example.techmoves.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private DistanceService distanceService;

    @InjectMocks
    private OrderService orderService;

    private OrderCreationDTO validOrderCreationDTO;
    private Order savedOrder;

    @BeforeEach
    void setUp() {
        validOrderCreationDTO = new OrderCreationDTO();
        validOrderCreationDTO.setOrigin(Arrays.asList("22.319181", "114.170008"));
        validOrderCreationDTO.setDestination(Arrays.asList("22.336093", "114.155288"));

        savedOrder = new Order(2350, OrderStatus.UNASSIGNED);
        savedOrder.setId(1L);
    }

    @Test
    void createOrder_ValidInput_ReturnsOrderResponse() {
        when(distanceService.calculateDistance(any(), any())).thenReturn(2350);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        OrderResponseDTO response = orderService.createOrder(validOrderCreationDTO);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(2350, response.getDistance());
        assertEquals("UNASSIGNED", response.getStatus());
    }

    @Test
    void takeOrder_ValidOrder_ChangesStatusToTaken() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(savedOrder));

        orderService.takeOrder(1L);

        assertEquals(OrderStatus.TAKEN, savedOrder.getStatus());
        verify(orderRepository, times(1)).save(savedOrder);
    }

    @Test
    void takeOrder_AlreadyTakenOrder_ThrowsException() {
        savedOrder.setStatus(OrderStatus.TAKEN);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(savedOrder));

        assertThrows(OrderAlreadyTakenException.class, () -> orderService.takeOrder(1L));
    }

    @Test
    void listOrders_ValidPageAndLimit_ReturnsOrders() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Order> page = new PageImpl<>(List.of(savedOrder), pageRequest, 1);
        when(orderRepository.findAll(pageRequest)).thenReturn(page);

        List<OrderResponseDTO> result = orderService.listOrders(1, 10);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void listOrders_InvalidPage_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> orderService.listOrders(0, 10));
    }

    @Test
    void listOrders_InvalidLimit_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> orderService.listOrders(1, 0));
        assertThrows(IllegalArgumentException.class, () -> orderService.listOrders(1, 101));
    }
}