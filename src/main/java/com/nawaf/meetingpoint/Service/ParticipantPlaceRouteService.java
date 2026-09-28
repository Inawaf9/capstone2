package com.nawaf.meetingpoint.Service;

import com.nawaf.meetingpoint.Model.ParticipantPlaceRoute;
import com.nawaf.meetingpoint.Repository.ParticipantPlaceRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipantPlaceRouteService {

    private final ParticipantPlaceRouteRepository participantPlaceRouteRepository;

    public List<ParticipantPlaceRoute> getRoutesForPlace(Integer placeId) {
        return participantPlaceRouteRepository.findParticipantPlaceRoutesByPlaceId(placeId);
    }

    public List<ParticipantPlaceRoute> getRoutesForParticipant(Integer participantId) {
        return participantPlaceRouteRepository.findParticipantPlaceRoutesByParticipantId(participantId);
    }

    public ParticipantPlaceRoute getRoute(Integer id) {
        return participantPlaceRouteRepository.findParticipantPlaceRouteById(id);
    }
}