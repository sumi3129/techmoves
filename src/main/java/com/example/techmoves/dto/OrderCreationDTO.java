package com.example.techmoves.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class OrderCreationDTO {
    @NotNull(message = "Origin is required")
    @Size(min = 2, max = 2, message = "Origin must contain exactly 2 elements")
    private List<String> origin;

    @NotNull(message = "Destination is required")
    @Size(min = 2, max = 2, message = "Destination must contain exactly 2 elements")
    private List<String> destination;

}