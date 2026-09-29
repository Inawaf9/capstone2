package com.nawaf.meetingpoint.Service;

import com.nawaf.meetingpoint.Api.ApiException;
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
    public void removePlace(Integer meetingId, Integer organizerId, Integer placeId) {
        Meeting meeting = meetingRepository.findMeetingById(meetingId);

        if (meeting == null) throw new ApiException("Meeting not found");

        MeetingRequest meetingRequest = meetingRequestRepository.findMeetingRequestById(meeting.getMeetingRequestId());

        if (meetingRequest == null) throw new ApiException("Meeting request not found");
        if (!meetingRequest.getOrganizerId().equals(organizerId)) throw new ApiException("User is not the organizer");
        if (!meeting.getStatus().equalsIgnoreCase("OPEN")) throw new ApiException("Meeting must be open");

        Place place = placeRepository.findPlaceById(placeId);

        if (place == null) throw new ApiException("Place not found");
        if (!place.getMeetingId().equals(meetingId)) throw new ApiException("Place does not belong to this meeting");

        participantPlaceRouteRepository.deleteParticipantPlaceRoutesByPlaceId(placeId);
        placeRepository.delete(place);
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
    public void selectPlaceForVoting(Integer meetingId, Integer participantId, Integer userId, SelectPlaceDTO placeDTO) {
        Meeting meeting = meetingRepository.findMeetingById(meetingId);

        if (meeting == null) throw new ApiException("Meeting not found");
        if (!meeting.getStatus().equalsIgnoreCase("OPEN")) throw new ApiException("Meeting must be open");

        Participant participant = participantRepository.findParticipantById(participantId);

        if (participant == null) throw new ApiException("Participant not found");
        if (!participant.getMeetingId().equals(meetingId)) throw new ApiException("Participant does not belong to this meeting");
        if (!participant.getUserId().equals(userId)) throw new ApiException("User does not own this participant");

        if (placeDTO.googlePlaceId() == null || placeDTO.googlePlaceId().isBlank()) throw new ApiException("Invalid place data");
        if (placeDTO.name() == null || placeDTO.name().isBlank()) throw new ApiException("Invalid place data");
        if (placeDTO.address() == null || placeDTO.address().isBlank()) throw new ApiException("Invalid place data");
        if (placeDTO.latitude() == null || placeDTO.longitude() == null) throw new ApiException("Invalid place data");
        if (placeDTO.latitude() < -90 || placeDTO.latitude() > 90) throw new ApiException("Invalid place data");
        if (placeDTO.longitude() < -180 || placeDTO.longitude() > 180) throw new ApiException("Invalid place data");
        if (placeDTO.rating() != null && (placeDTO.rating() < 0 || placeDTO.rating() > 5)) throw new ApiException("Invalid place data");
        if (placeDTO.aiScore() != null && (placeDTO.aiScore() < 0 || placeDTO.aiScore() > 100)) throw new ApiException("Invalid place data");

        Place oldPlace = placeRepository.findPlaceByMeetingIdAndGooglePlaceId(meetingId, placeDTO.googlePlaceId());

        if (oldPlace != null) throw new ApiException("Place already selected for voting");

        List<Participant> participants = participantRepository.findParticipantsByMeetingId(meetingId);

        if (participants.isEmpty()) throw new ApiException("Some participants have not provided their location");

        for (Participant meetingParticipant : participants) {
            if (meetingParticipant.getLatitude() == null || meetingParticipant.getLongitude() == null) throw new ApiException("Some participants have not provided their location");
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
    }

    public String getGoogleMapsUrl(Integer placeId) {
        Place place = placeRepository.findPlaceById(placeId);

        if (place == null) return null;

        String placeName = URLEncoder.encode(place.getName(), StandardCharsets.UTF_8);

        return "https://www.google.com/maps/search/?api=1&query=" + placeName + "&query_place_id=" + place.getGooglePlaceId();
    }
}