package com.nawaf.meetingpoint.DTO.Recommendation;

public record SelectPlaceDTO(
        String googlePlaceId,
        String name,
        String address,
        Double latitude,
        Double longitude,
        Double rating,
        Integer aiScore,
        String aiReason
){}