package com.nawaf.meetingpoint.Repository;

import com.nawaf.meetingpoint.Model.ParticipantPlaceRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipantPlaceRouteRepository extends JpaRepository<ParticipantPlaceRoute, Integer> {

    ParticipantPlaceRoute findParticipantPlaceRouteById(Integer id);

    List<ParticipantPlaceRoute> findParticipantPlaceRoutesByPlaceId(Integer placeId);

    List<ParticipantPlaceRoute> findParticipantPlaceRoutesByParticipantId(Integer participantId);

    void deleteParticipantPlaceRoutesByPlaceId(Integer placeId);
}