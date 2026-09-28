package com.nawaf.meetingpoint.Repository;

import com.nawaf.meetingpoint.Model.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Integer> {

    Place findPlaceById(Integer id);

    Place findPlaceByMeetingIdAndGooglePlaceId(Integer meetingId, String googlePlaceId);

    List<Place> findPlacesByMeetingId(Integer meetingId);

    long countPlacesByMeetingId(Integer meetingId);
}