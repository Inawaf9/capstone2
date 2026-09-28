package com.nawaf.meetingpoint.DTO.GooglePlaces;

import java.util.List;

public record GooglePlacesRequest(
        List<String> includedTypes,
        Integer maxResultCount,
        LocationRestriction locationRestriction
) {

    public record LocationRestriction(
            Circle circle
    ) {}

    public record Circle(
            Center center,
            Double radius
    ) {}

    public record Center(
            Double latitude,
            Double longitude
    ) {}
}