package com.example.techmoves.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class OrderTakingDTO {
    @NotNull(message = "Status is required")
    @Pattern(regexp = "TAKEN", message = "Status must be 'TAKEN'")
    private String status;

}