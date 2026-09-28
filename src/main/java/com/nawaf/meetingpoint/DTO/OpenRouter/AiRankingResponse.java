package com.nawaf.meetingpoint.DTO.OpenRouter;

import java.util.List;

public record AiRankingResponse(
        List<RankedPlace> places
) {

    public record RankedPlace(
            String googlePlaceId,
            Integer rank,
            Integer aiScore,
            String aiReason
    ) {}
}