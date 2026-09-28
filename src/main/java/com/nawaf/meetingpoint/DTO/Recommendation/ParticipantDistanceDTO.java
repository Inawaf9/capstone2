package com.nawaf.meetingpoint.DTO.Recommendation;

public record ParticipantDistanceDTO(
        Integer participantId,
        Integer userId, Double distanceKm
) {
}