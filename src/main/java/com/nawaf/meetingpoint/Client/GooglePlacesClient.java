package com.nawaf.meetingpoint.Client;

import com.nawaf.meetingpoint.DTO.GooglePlaces.GooglePlacesRequest;
import com.nawaf.meetingpoint.DTO.GooglePlaces.GooglePlacesResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GooglePlacesClient {

    private final RestClient restClient;
    private final String apiKey;

    public GooglePlacesClient(RestClient.Builder builder, @Value("${google.maps.api-key}") String apiKey, @Value("${google.places.base-url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
        this.apiKey = apiKey;
    }

    public GooglePlacesResponse searchNearby(GooglePlacesRequest request) {
        return restClient.post().uri("/v1/places:searchNearby").header("X-Goog-Api-Key", apiKey).header("X-Goog-FieldMask", "places.id,places.displayName,places.formattedAddress,places.location,places.rating").body(request).retrieve().body(GooglePlacesResponse.class);
    }
}