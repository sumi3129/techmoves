package com.example.techmoves.integration;

import com.example.techmoves.dto.OrderCreationDTO;
import com.example.techmoves.dto.OrderTakingDTO;
import com.example.techmoves.model.Order;
import com.example.techmoves.model.OrderStatus;
import com.example.techmoves.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        testOrder = new Order(2350, OrderStatus.UNASSIGNED);
        testOrder = orderRepository.save(testOrder);
    }

    @Test
    void createOrder_ValidInput_ReturnsCreatedOrder() throws Exception {
        OrderCreationDTO orderCreationDTO = new OrderCreationDTO();
        orderCreationDTO.setOrigin(Arrays.asList("22.319181", "114.170008"));
        orderCreationDTO.setDestination(Arrays.asList("22.336093", "114.155288"));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderCreationDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.distance", greaterThan(0)))
                .andExpect(jsonPath("$.status", is("UNASSIGNED")));
    }

    @Test
    void createOrder_InvalidInput_ReturnsBadRequest() throws Exception {
        OrderCreationDTO invalidOrder = new OrderCreationDTO();
        invalidOrder.setOrigin(Arrays.asList("invalid", "coordinates"));
        invalidOrder.setDestination(Arrays.asList("22.336093", "114.155288"));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidOrder)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation failed")));
    }

    @Test
    void takeOrder_ValidOrder_ReturnsSuccess() throws Exception {
        OrderTakingDTO orderTakingDTO = new OrderTakingDTO();
        orderTakingDTO.setStatus("TAKEN");

        mockMvc.perform(patch("/orders/" + testOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderTakingDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("SUCCESS")));
    }

    @Test
    void takeOrder_AlreadyTakenOrder_ReturnsConflict() throws Exception {
        testOrder.setStatus(OrderStatus.TAKEN);
        orderRepository.save(testOrder);

        OrderTakingDTO orderTakingDTO = new OrderTakingDTO();
        orderTakingDTO.setStatus("TAKEN");

        mockMvc.perform(patch("/orders/" + testOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderTakingDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", containsString("already taken")));
    }

    @Test
    void listOrders_ValidParameters_ReturnsOrders() throws Exception {
        mockMvc.perform(get("/orders?page=1&limit=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(testOrder.getId().intValue())));
    }

    @Test
    void listOrders_InvalidParameters_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/orders?page=0&limit=10"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/orders?page=1&limit=0"))
                .andExpect(status().isBadRequest());
    }
}