package com.nawaf.meetingpoint.Service;

import com.nawaf.meetingpoint.Client.GooglePlacesClient;
import com.nawaf.meetingpoint.Client.OpenRouterClient;
import com.nawaf.meetingpoint.DTO.GooglePlaces.GooglePlacesRequest;
import com.nawaf.meetingpoint.DTO.GooglePlaces.GooglePlacesResponse;
import com.nawaf.meetingpoint.DTO.OpenRouter.AiRankingResponse;
import com.nawaf.meetingpoint.DTO.Recommendation.ParticipantDistanceDTO;
import com.nawaf.meetingpoint.DTO.Recommendation.RecommendationDTO;
import com.nawaf.meetingpoint.Model.Meeting;
import com.nawaf.meetingpoint.Model.MeetingRequest;
import com.nawaf.meetingpoint.Model.MeetingRequestMember;
import com.nawaf.meetingpoint.Model.Participant;
import com.nawaf.meetingpoint.Model.Place;
import com.nawaf.meetingpoint.Model.User;
import com.nawaf.meetingpoint.Repository.MeetingRepository;
import com.nawaf.meetingpoint.Repository.MeetingRequestMemberRepository;
import com.nawaf.meetingpoint.Repository.MeetingRequestRepository;
import com.nawaf.meetingpoint.Repository.ParticipantRepository;
import com.nawaf.meetingpoint.Repository.PlaceRepository;
import com.nawaf.meetingpoint.Repository.UserRepository;
import com.nawaf.meetingpoint.Repository.VoteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final MeetingRequestRepository meetingRequestRepository;
    private final MeetingRequestMemberRepository meetingRequestMemberRepository;
    private final ParticipantRepository participantRepository;
    private final GooglePlacesClient googlePlacesClient;
    private final DistanceService distanceService;
    private final FairnessService fairnessService;
    private final OpenRouterClient openRouterClient;
    private final PlaceRepository placeRepository;
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public List<Meeting> getMeetings() {
        return meetingRepository.findAll();
    }

    public Meeting getMeeting(Integer id) {
        return meetingRepository.findMeetingById(id);
    }

    // 0 = Meeting started successfully
    // 1 = Meeting request not found
    // 2 = User is not the organizer
    // 3 = Meeting request is canceled
    // 4 = Meeting already started
    // 5 = No accepted members
    @Transactional
    public int startMeeting(Integer requestId, Integer organizerId) {
        MeetingRequest meetingRequest = meetingRequestRepository.findMeetingRequestById(requestId);

        if (meetingRequest == null) return 1;
        if (!meetingRequest.getOrganizerId().equals(organizerId)) return 2;
        if (meetingRequest.getStatus().equalsIgnoreCase("CANCELLED")) return 3;

        Meeting oldMeeting = meetingRepository.findMeetingByMeetingRequestId(requestId);

        if (oldMeeting != null) return 4;

        List<MeetingRequestMember> acceptedMembers = meetingRequestMemberRepository.findMeetingRequestMembersByMeetingRequestIdAndStatus(requestId, "ACCEPTED");

        if (acceptedMembers.isEmpty()) return 5;

        Meeting meeting = new Meeting();

        meeting.setMeetingRequestId(requestId);
        meeting.setStatus("OPEN");

        meetingRepository.save(meeting);

        Participant organizerParticipant = new Participant();

        organizerParticipant.setMeetingId(meeting.getId());
        organizerParticipant.setUserId(organizerId);

        participantRepository.save(organizerParticipant);

        for (MeetingRequestMember member : acceptedMembers) {
            if (member.getUserId() == null) continue;
            if (member.getUserId().equals(organizerId)) continue;

            Participant participant = new Participant();

            participant.setMeetingId(meeting.getId());
            participant.setUserId(member.getUserId());

            participantRepository.save(participant);
        }

        meetingRequest.setStatus("READY");
        meetingRequestRepository.save(meetingRequest);

        return 0;
    }

    // 0 = Meeting canceled successfully
    // 1 = Meeting not found
    // 2 = User is not the organizer
    // 3 = Meeting already canceled
    // 4 = Meeting already confirmed
    // 5 = Meeting request not found
    public int cancelMeeting(Integer meetingId, Integer organizerId) {
        Meeting meeting = meetingRepository.findMeetingById(meetingId);

        if (meeting == null) return 1;

        MeetingRequest meetingRequest = meetingRequestRepository.findMeetingRequestById(meeting.getMeetingRequestId());

        if (meetingRequest == null) return 5;
        if (!meetingRequest.getOrganizerId().equals(organizerId)) return 2;
        if (meeting.getStatus().equalsIgnoreCase("CANCELLED")) return 3;
        if (meeting.getStatus().equalsIgnoreCase("CONFIRMED")) return 4;

        meeting.setStatus("CANCELLED");
        meetingRepository.save(meeting);

        return 0;
    }

    public List<Participant> getMissingLocations(Integer meetingId) {
        return participantRepository.findParticipantsWithMissingLocation(meetingId);
    }

    public boolean isLocationReady(Integer meetingId) {
        long totalParticipants = participantRepository.countParticipantsByMeetingId(meetingId);
        long missingLocations = participantRepository.countParticipantsWithMissingLocation(meetingId);

        return totalParticipants > 0 && missingLocations == 0;
    }

    // 0 = Center calculated successfully
    // 1 = Meeting not found
    // 2 = User is not the organizer
    // 3 = Meeting must be open
    // 4 = Some participants have not provided their location
    // 5 = Meeting request not found
    public int calculateCenter(Integer meetingId, Integer organizerId) {
        Meeting meeting = meetingRepository.findMeetingById(meetingId);

        if (meeting == null) return 1;

        MeetingRequest meetingRequest = meetingRequestRepository.findMeetingRequestById(meeting.getMeetingRequestId());

        if (meetingRequest == null) return 5;
        if (!meetingRequest.getOrganizerId().equals(organizerId)) return 2;
        if (!meeting.getStatus().equalsIgnoreCase("OPEN")) return 3;
        if (!isLocationReady(meetingId)) return 4;

        Double averageLatitude = participantRepository.findAverageLatitudeByMeetingId(meetingId);
        Double averageLongitude = participantRepository.findAverageLongitudeByMeetingId(meetingId);

        if (averageLatitude == null || averageLongitude == null) return 4;

        meeting.setCenterLatitude(averageLatitude);
        meeting.setCenterLongitude(averageLongitude);

        meetingRepository.save(meeting);

        return 0;
    }

    public List<GooglePlacesResponse.PlaceResult> getNearbyPlaces(Integer meetingId) {
        Meeting meeting = meetingRepository.findMeetingById(meetingId);

        if (meeting == null) return null;
        if (meeting.getCenterLatitude() == null || meeting.getCenterLongitude() == null) return new ArrayList<>();

        MeetingRequest meetingRequest = meetingRequestRepository.findMeetingRequestById(meeting.getMeetingRequestId());

        if (meetingRequest == null) return new ArrayList<>();

        GooglePlacesRequest.Circle circle = new GooglePlacesRequest.Circle(new GooglePlacesRequest.Center(meeting.getCenterLatitude(), meeting.getCenterLongitude()), 5000.0);

        GooglePlacesRequest.LocationRestriction locationRestriction = new GooglePlacesRequest.LocationRestriction(circle);

        GooglePlacesRequest request = new GooglePlacesRequest(List.of(meetingRequest.getCategory()), 5, locationRestriction);

        GooglePlacesResponse response = googlePlacesClient.searchNearby(request);

        if (response == null || response.places() == null) return new ArrayList<>();

        return response.places();
    }

    public List<RecommendationDTO> calculatePlaceRecommendations(Integer meetingId) {
        Meeting meeting = meetingRepository.findMeetingById(meetingId);

        if (meeting == null) return null;

        List<Participant> participants = participantRepository.findParticipantsByMeetingId(meetingId);

        if (participants.isEmpty()) return new ArrayList<>();

        for (Participant participant : participants) {
            if (participant.getLatitude() == null || participant.getLongitude() == null) return new ArrayList<>();
        }

        List<GooglePlacesResponse.PlaceResult> nearbyPlaces = getNearbyPlaces(meetingId);

        if (nearbyPlaces == null || nearbyPlaces.isEmpty()) return new ArrayList<>();

        List<RecommendationDTO> recommendations = new ArrayList<>();

        for (GooglePlacesResponse.PlaceResult googlePlace : nearbyPlaces) {
            if (googlePlace.location() == null) continue;

            List<Double> distanceValues = new ArrayList<>();
            List<ParticipantDistanceDTO> distances = new ArrayList<>();

            for (Participant participant : participants) {
                double distance = distanceService.calculateDistance(participant.getLatitude(), participant.getLongitude(), googlePlace.location().latitude(), googlePlace.location().longitude());

                distanceValues.add(distance);

                ParticipantDistanceDTO participantDistance = new ParticipantDistanceDTO(participant.getId(), participant.getUserId(), distance);

                distances.add(participantDistance);
            }

            double averageDistance = fairnessService.calculateAverageDistance(distanceValues);
            double maxDistance = fairnessService.calculateMaxDistance(distanceValues);
            double fairnessScore = fairnessService.calculateFairnessScore(distanceValues);

            RecommendationDTO recommendation = new RecommendationDTO(
                    googlePlace.id(),
                    googlePlace.displayName() != null ? googlePlace.displayName().text() : null,
                    googlePlace.formattedAddress(),
                    googlePlace.location().latitude(),
                    googlePlace.location().longitude(),
                    googlePlace.rating(),
                    averageDistance,
                    maxDistance,
                    fairnessScore,
                    null,
                    null,
                    null,
                    distances
            );

            recommendations.add(recommendation);
        }

        return recommendations;
    }

    private String buildRankingPrompt(List<RecommendationDTO> recommendations) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Rank the following meeting places from best to worst.\n");
        prompt.append("Consider fairness score, average distance, maximum distance, and rating.\n");
        prompt.append("Fairness score is from 0 to 100, where a higher score is better.\n");
        prompt.append("Return a rank, AI score from 0 to 100, and a short reason for each place.\n");
        prompt.append("Use the exact Google Place ID provided for every place.\n");
        prompt.append("Do not invent any new places.\n\n");

        for (RecommendationDTO recommendation : recommendations) {
            prompt.append("Google Place ID: ").append(recommendation.googlePlaceId()).append("\n");
            prompt.append("Name: ").append(recommendation.name()).append("\n");
            prompt.append("Rating: ").append(recommendation.rating()).append("\n");
            prompt.append("Average Distance: ").append(recommendation.averageDistance()).append(" km\n");
            prompt.append("Maximum Distance: ").append(recommendation.maxDistance()).append(" km\n");
            prompt.append("Fairness Score: ").append(recommendation.fairnessScore()).append("\n\n");
        }

        return prompt.toString();
    }

    private List<RecommendationDTO> addAiRanking(List<RecommendationDTO> recommendations) {
        String prompt = buildRankingPrompt(recommendations);

        AiRankingResponse aiResponse = openRouterClient.rankPlaces(prompt);

        if (aiResponse == null || aiResponse.places() == null) return recommendations;

        List<RecommendationDTO> rankedRecommendations = new ArrayList<>();

        for (RecommendationDTO recommendation : recommendations) {
            Integer aiRank = null;
            Integer aiScore = null;
            String aiReason = null;

            for (AiRankingResponse.RankedPlace rankedPlace : aiResponse.places()) {
                if (rankedPlace.googlePlaceId() != null && rankedPlace.googlePlaceId().equals(recommendation.googlePlaceId())) {
                    aiRank = rankedPlace.rank();
                    aiScore = rankedPlace.aiScore();
                    aiReason = rankedPlace.aiReason();
                    break;
                }
            }

            RecommendationDTO rankedRecommendation = new RecommendationDTO(
                    recommendation.googlePlaceId(),
                    recommendation.name(),
                    recommendation.address(),
                    recommendation.latitude(),
                    recommendation.longitude(),
                    recommendation.rating(),
                    recommendation.averageDistance(),
                    recommendation.maxDistance(),
                    recommendation.fairnessScore(),
                    aiRank,
                    aiScore,
                    aiReason,
                    recommendation.distances()
            );

            rankedRecommendations.add(rankedRecommendation);
        }

        rankedRecommendations.sort(Comparator.comparing(RecommendationDTO::aiRank, Comparator.nullsLast(Comparator.naturalOrder())));

        return rankedRecommendations;
    }

    public List<RecommendationDTO> generateRecommendations(Integer meetingId) {
        List<RecommendationDTO> recommendations = calculatePlaceRecommendations(meetingId);

        if (recommendations == null) return null;
        if (recommendations.isEmpty()) return recommendations;

        return addAiRanking(recommendations);
    }

    // 0 = Voting started successfully
    // 1 = Meeting not found
    // 2 = User is not the organizer
    // 3 = Meeting must be open
    // 4 = At least two places are required
    // 5 = Meeting request not found
    public int startVoting(Integer meetingId, Integer organizerId) {
        Meeting meeting = meetingRepository.findMeetingById(meetingId);

        if (meeting == null) return 1;

        MeetingRequest meetingRequest = meetingRequestRepository.findMeetingRequestById(meeting.getMeetingRequestId());

        if (meetingRequest == null) return 5;
        if (!meetingRequest.getOrganizerId().equals(organizerId)) return 2;
        if (!meeting.getStatus().equalsIgnoreCase("OPEN")) return 3;

        long placesCount = placeRepository.countPlacesByMeetingId(meetingId);

        if (placesCount < 2) return 4;

        meeting.setStatus("VOTING");
        meetingRepository.save(meeting);

        return 0;
    }

    // 0 = Meeting confirmed successfully
    // 1 = Meeting not found
    // 2 = User is not the organizer
    // 3 = Meeting must be in voting status
    // 4 = No places found
    // 5 = Not all participants have voted
    // 6 = Meeting request not found
    @Transactional
    public int confirmMeeting(Integer meetingId, Integer organizerId) {
        Meeting meeting = meetingRepository.findMeetingById(meetingId);

        if (meeting == null) return 1;

        MeetingRequest meetingRequest = meetingRequestRepository.findMeetingRequestById(meeting.getMeetingRequestId());

        if (meetingRequest == null) return 6;
        if (!meetingRequest.getOrganizerId().equals(organizerId)) return 2;
        if (!meeting.getStatus().equalsIgnoreCase("VOTING")) return 3;

        List<Place> places = placeRepository.findPlacesByMeetingId(meetingId);

        if (places.isEmpty()) return 4;

        long totalParticipants = participantRepository.countParticipantsByMeetingId(meetingId);
        long totalVotes = 0;

        for (Place place : places) {
            totalVotes += voteRepository.countVotesByPlaceId(place.getId());
        }

        if (totalParticipants == 0 || totalVotes < totalParticipants) return 5;

        Place winner = null;
        long highestVotes = -1;

        for (Place place : places) {
            long placeVotes = voteRepository.countVotesByPlaceId(place.getId());

            if (winner == null || placeVotes > highestVotes) {
                winner = place;
                highestVotes = placeVotes;
            } else if (placeVotes == highestVotes) {
                double currentFairness = place.getFairnessScore() != null ? place.getFairnessScore() : 0;
                double winnerFairness = winner.getFairnessScore() != null ? winner.getFairnessScore() : 0;

                if (currentFairness > winnerFairness) {
                    winner = place;
                }
            }
        }

        meeting.setSelectedPlaceId(winner.getId());
        meeting.setStatus("CONFIRMED");

        meetingRepository.save(meeting);

        String placeName = URLEncoder.encode(winner.getName(), StandardCharsets.UTF_8);
        String googleMapsUrl = "https://www.google.com/maps/search/?api=1&query=" + placeName + "&query_place_id=" + winner.getGooglePlaceId();

        List<Participant> participants = participantRepository.findParticipantsByMeetingId(meetingId);

        for (Participant participant : participants) {
            User user = userRepository.findUserById(participant.getUserId());

            if (user != null) {
                emailService.sendMeetingConfirmedEmail(user.getEmail(), user.getName(), meetingRequest.getName(), winner.getName(), googleMapsUrl);
            }
        }

        return 0;
    }
}