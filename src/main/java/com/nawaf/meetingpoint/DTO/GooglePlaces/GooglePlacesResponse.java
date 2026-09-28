package com.nawaf.meetingpoint.DTO.GooglePlaces;

import java.util.List;

public record GooglePlacesResponse(
        List<PlaceResult> places
) {

    public record PlaceResult(
            String id,
            DisplayName displayName,
            String formattedAddress,
            Location location,
            Double rating
    ) {}

    public record DisplayName(
            String text,
            String languageCode
    ) {}

    public record Location(
            Double latitude,
            Double longitude
    ) {}
}