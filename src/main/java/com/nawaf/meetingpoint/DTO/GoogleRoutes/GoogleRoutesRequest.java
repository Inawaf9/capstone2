package com.nawaf.meetingpoint.DTO.GoogleRoutes;

import java.util.List;

public record GoogleRoutesRequest(
        List<RoutePoint> origins,
        List<RoutePoint> destinations,
        String travelMode,
        String routingPreference
) {

    public record RoutePoint(
            Waypoint waypoint
    ) {}

    public record Waypoint(
            Location location
    ) {}

    public record Location(
            LatLng latLng
    ) {}

    public record LatLng(
            Double latitude,
            Double longitude
    ) {}
}