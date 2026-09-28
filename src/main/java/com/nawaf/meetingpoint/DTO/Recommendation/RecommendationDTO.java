package com.nawaf.meetingpoint.DTO.Recommendation;

import java.util.List;

public record RecommendationDTO(
        String googlePlaceId,

        String name,

        String address,

        Double latitude,

        Double longitude,

        Double rating,

        Double averageDistance,

        Double maxDistance,

        Double fairnessScore,

        Integer aiRank,

        Integer aiScore,

        String aiReason,

        List<ParticipantDistanceDTO> distances

) {
}