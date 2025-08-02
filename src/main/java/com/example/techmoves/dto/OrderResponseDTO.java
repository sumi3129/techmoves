package com.example.techmoves.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderResponseDTO {
    private Long id;
    private Integer distance;
    private String status;

    public OrderResponseDTO() {
    }

    public OrderResponseDTO(Long id, Integer distance, String status) {
        this.id = id;
        this.distance = distance;
        this.status = status;
    }

}