package com.nawaf.meetingpoint.Service;

import com.nawaf.meetingpoint.DTO.Recommendation.SelectPlaceDTO;
import com.nawaf.meetingpoint.Model.Meeting;
import com.nawaf.meetingpoint.Model.MeetingRequest;
import com.nawaf.meetingpoint.Model.Participant;
import com.nawaf.meetingpoint.Model.ParticipantPlaceRoute;
import com.nawaf.meetingpoint.Model.Place;
import com.nawaf.meetingpoint.Repository.MeetingRepository;
import com.nawaf.meetingpoint.Repository.MeetingRequestRepository;
import com.nawaf.meetingpoint.Repository.ParticipantPlaceRouteRepository;
import com.nawaf.meetingpoint.Repository.ParticipantRepository;
import com.nawaf.meetingpoint.Repository.PlaceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final MeetingRepository meetingRepository;
    private final MeetingRequestRepository meetingRequestRepository;
    private final ParticipantRepository participantRepository;
    private final ParticipantPlaceRouteRepository participantPlaceRouteRepository;
    private final DistanceService distanceService;
    private final FairnessService fairnessService;

    public List<Place> getPlaces(Integer meetingId) {
        return placeRepository.findPlacesByMeetingId(meetingId);
    }

    public Place getPlace(Integer id) {
        return placeRepository.findPlaceById(id);
    }

    // 0 = Place removed successfully
    // 1 = Meeting not found
    // 2 = User is not the organizer
    // 3 = Meeting must be open
    // 4 = Place not found
    // 5 = Place does not belong to this meeting
    // 6 = Meeting request not found
    @Transactional
    public int removePlace(Integer meetingId, Integer organizerId, Integer placeId) {
        Meeting meeting = meetingRepository.findMeetingById(meetingId);

        if (meeting == null) return 1;

        MeetingRequest meetingRequest = meetingRequestRepository.findMeetingRequestById(meeting.getMeetingRequestId());

        if (meetingRequest == null) return 6;
        if (!meetingRequest.getOrganizerId().equals(organizerId)) return 2;
        if (!meeting.getStatus().equalsIgnoreCase("OPEN")) return 3;

        Place place = placeRepository.findPlaceById(placeId);

        if (place == null) return 4;
        if (!place.getMeetingId().equals(meetingId)) return 5;

        participantPlaceRouteRepository.deleteParticipantPlaceRoutesByPlaceId(placeId);
        placeRepository.delete(place);

        return 0;
    }

    // 0 = Place added to voting successfully
    // 1 = Meeting not found
    // 2 = Meeting must be open
    // 3 = Participant not found
    // 4 = Participant does not belong to this meeting
    // 5 = User does not own this participant
    // 6 = Place already selected for voting
    // 7 = Invalid place data
    // 8 = Some participants have not provided their location
    @Transactional
    public int selectPlaceForVoting(Integer meetingId, Integer participantId, Integer userId, SelectPlaceDTO placeDTO) {
        Meeting meeting = meetingRepository.findMeetingById(meetingId);

        if (meeting == null) return 1;
        if (!meeting.getStatus().equalsIgnoreCase("OPEN")) return 2;

        Participant participant = participantRepository.findParticipantById(participantId);

        if (participant == null) return 3;
        if (!participant.getMeetingId().equals(meetingId)) return 4;
        if (!participant.getUserId().equals(userId)) return 5;

        if (placeDTO.googlePlaceId() == null || placeDTO.googlePlaceId().isBlank()) return 7;
        if (placeDTO.name() == null || placeDTO.name().isBlank()) return 7;
        if (placeDTO.address() == null || placeDTO.address().isBlank()) return 7;
        if (placeDTO.latitude() == null || placeDTO.longitude() == null) return 7;
        if (placeDTO.latitude() < -90 || placeDTO.latitude() > 90) return 7;
        if (placeDTO.longitude() < -180 || placeDTO.longitude() > 180) return 7;
        if (placeDTO.rating() != null && (placeDTO.rating() < 0 || placeDTO.rating() > 5)) return 7;
        if (placeDTO.aiScore() != null && (placeDTO.aiScore() < 0 || placeDTO.aiScore() > 100)) return 7;

        Place oldPlace = placeRepository.findPlaceByMeetingIdAndGooglePlaceId(meetingId, placeDTO.googlePlaceId());

        if (oldPlace != null) return 6;

        List<Participant> participants = participantRepository.findParticipantsByMeetingId(meetingId);

        if (participants.isEmpty()) return 8;

        for (Participant meetingParticipant : participants) {
            if (meetingParticipant.getLatitude() == null || meetingParticipant.getLongitude() == null) return 8;
        }

        List<Double> distances = new ArrayList<>();

        for (Participant meetingParticipant : participants) {
            double distance = distanceService.calculateDistance(meetingParticipant.getLatitude(), meetingParticipant.getLongitude(), placeDTO.latitude(), placeDTO.longitude());

            distances.add(distance);
        }

        double fairnessScore = fairnessService.calculateFairnessScore(distances);

        Place place = new Place();

        place.setMeetingId(meetingId);
        place.setGooglePlaceId(placeDTO.googlePlaceId());
        place.setName(placeDTO.name());
        place.setAddress(placeDTO.address());
        place.setLatitude(placeDTO.latitude());
        place.setLongitude(placeDTO.longitude());
        place.setRating(placeDTO.rating());
        place.setFairnessScore(fairnessScore);

        if (placeDTO.aiScore() != null) place.setAiScore(Double.valueOf(placeDTO.aiScore()));

        place.setAiReason(placeDTO.aiReason());

        placeRepository.save(place);

        for (Participant meetingParticipant : participants) {
            double distance = distanceService.calculateDistance(meetingParticipant.getLatitude(), meetingParticipant.getLongitude(), place.getLatitude(), place.getLongitude());

            ParticipantPlaceRoute route = new ParticipantPlaceRoute();

            route.setParticipantId(meetingParticipant.getId());
            route.setPlaceId(place.getId());
            route.setDistanceKm(distance);

            participantPlaceRouteRepository.save(route);
        }

        return 0;
    }

    public String getGoogleMapsUrl(Integer placeId) {
        Place place = placeRepository.findPlaceById(placeId);

        if (place == null) return null;

        String placeName = URLEncoder.encode(place.getName(), StandardCharsets.UTF_8);

        return "https://www.google.com/maps/search/?api=1&query=" + placeName + "&query_place_id=" + place.getGooglePlaceId();
    }
}