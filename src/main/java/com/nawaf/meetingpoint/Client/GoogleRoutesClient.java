package com.nawaf.meetingpoint.Client;

import com.nawaf.meetingpoint.DTO.GoogleRoutes.GoogleRoutesRequest;
import com.nawaf.meetingpoint.DTO.GoogleRoutes.GoogleRoutesResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class GoogleRoutesClient {

    private final RestClient restClient;
    private final String apiKey;

    public GoogleRoutesClient(RestClient.Builder builder, @Value("${google.maps.api-key}") String apiKey, @Value("${google.routes.base-url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
        this.apiKey = apiKey;
    }

    public List<GoogleRoutesResponse> calculateRoutes(GoogleRoutesRequest request) {
        return restClient.post().uri("/distanceMatrix/v2:computeRouteMatrix").header("X-Goog-Api-Key", apiKey).header("X-Goog-FieldMask", "originIndex,destinationIndex,status,condition,distanceMeters,duration").body(request).retrieve().body(new ParameterizedTypeReference<>() {});
    }
}