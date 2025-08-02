package com.example.techmoves.service;

import com.example.techmoves.exception.GoogleApiException;
import com.example.techmoves.exception.InvalidCoordinateException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;

@Service
public class DistanceService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${google.maps.api.key}")
    private String apiKey;

    @Value("${google.maps.api.distance-matrix-url}")
    private String distanceMatrixUrl;

    public DistanceService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public int calculateDistance(List<String> origin, List<String> destination) {
        validateCoordinates(origin, destination);

        String url = UriComponentsBuilder.fromHttpUrl(distanceMatrixUrl)
                .queryParam("origins", String.join(",", origin))
                .queryParam("destinations", String.join(",", destination))
                .queryParam("key", apiKey)
                .toUriString();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode elements = root.path("rows").get(0).path("elements").get(0);

            if ("OK".equals(elements.path("status").asText())) {
                return elements.path("distance").path("value").asInt();
            } else {
                throw new GoogleApiException("Unable to calculate distance: " + elements.path("status").asText());
            }
        } catch (IOException e) {
            throw new GoogleApiException("Error parsing Google Maps API response", e);
        }
    }

    private void validateCoordinates(List<String> origin, List<String> destination) {
        try {
            Double.parseDouble(origin.get(0));
            Double.parseDouble(origin.get(1));
            Double.parseDouble(destination.get(0));
            Double.parseDouble(destination.get(1));
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            throw new InvalidCoordinateException("Invalid coordinate format");
        }
    }
}