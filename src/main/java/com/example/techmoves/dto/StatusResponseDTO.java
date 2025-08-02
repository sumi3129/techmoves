package com.example.techmoves.dto;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StatusResponseDTO {
    private String status;

    public StatusResponseDTO() {
    }

    public StatusResponseDTO(String status) {
        this.status = status;
    }

}