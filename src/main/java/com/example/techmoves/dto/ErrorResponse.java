package com.example.techmoves.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ErrorResponse {
    private String error;
    private List<String> details;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public ErrorResponse(String error, List<String> details) {
        this.error = error;
        this.details = details;
    }

}