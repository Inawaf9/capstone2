package com.nawaf.meetingpoint.DTO.GoogleRoutes;

public record GoogleRoutesResponse(
        Integer originIndex,
        Integer destinationIndex,
        Status status,
        String condition,
        Integer distanceMeters,
        String duration
) {

    public record Status(
            Integer code,
            String message
    ) {}
}