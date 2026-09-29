package com.nawaf.meetingpoint.Service;

import com.nawaf.meetingpoint.Api.ApiException;
import com.nawaf.meetingpoint.Model.Meeting;
import com.nawaf.meetingpoint.Model.Participant;
import com.nawaf.meetingpoint.Model.Place;
import com.nawaf.meetingpoint.Model.Vote;
import com.nawaf.meetingpoint.Repository.MeetingRepository;
import com.nawaf.meetingpoint.Repository.ParticipantRepository;
import com.nawaf.meetingpoint.Repository.PlaceRepository;
import com.nawaf.meetingpoint.Repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final MeetingRepository meetingRepository;
    private final ParticipantRepository participantRepository;
    private final PlaceRepository placeRepository;

    // 0 = Vote submitted successfully
    // 1 = Meeting not found
    // 2 = Meeting is not in voting status
    // 3 = Participant not found
    // 4 = Participant does not belong to this meeting
    // 5 = User does not own this participant
    // 6 = Place not found
    // 7 = Place does not belong to this meeting
    // 8 = Participant already voted
    public void vote(Integer meetingId, Integer participantId, Integer userId, Integer placeId) {
        Meeting meeting = meetingRepository.findMeetingById(meetingId);

        if (meeting == null) throw new ApiException("Meeting not found");
        if (!meeting.getStatus().equalsIgnoreCase("VOTING")) throw new ApiException("Meeting is not in voting status");

        Participant participant = participantRepository.findParticipantById(participantId);

        if (participant == null) throw new ApiException("Participant not found");
        if (!participant.getMeetingId().equals(meetingId)) throw new ApiException("Participant does not belong to this meeting");
        if (!participant.getUserId().equals(userId)) throw new ApiException("User does not own this participant");

        Place place = placeRepository.findPlaceById(placeId);

        if (place == null) throw new ApiException("Place not found");
        if (!place.getMeetingId().equals(meetingId)) throw new ApiException("Place does not belong to this meeting");

        Vote oldVote = voteRepository.findVoteByParticipantId(participantId);

        if (oldVote != null) throw new ApiException("Participant already voted");

        Vote vote = new Vote();

        vote.setParticipantId(participantId);
        vote.setPlaceId(placeId);

        voteRepository.save(vote);
    }

    // 0 = Vote changed successfully
    // 1 = Meeting not found
    // 2 = Meeting is not in voting status
    // 3 = Participant not found
    // 4 = Participant does not belong to this meeting
    // 5 = User does not own this participant
    // 6 = Place not found
    // 7 = Place does not belong to this meeting
    // 8 = Vote not found
    // 9 = Participant already voted for this place
    public void changeVote(Integer meetingId, Integer participantId, Integer userId, Integer placeId) {
        Meeting meeting = meetingRepository.findMeetingById(meetingId);

        if (meeting == null) throw new ApiException("Meeting not found");
        if (!meeting.getStatus().equalsIgnoreCase("VOTING")) throw new ApiException("Meeting is not in voting status");

        Participant participant = participantRepository.findParticipantById(participantId);

        if (participant == null) throw new ApiException("Participant not found");
        if (!participant.getMeetingId().equals(meetingId)) throw new ApiException("Participant does not belong to this meeting");
        if (!participant.getUserId().equals(userId)) throw new ApiException("User does not own this participant");

        Place place = placeRepository.findPlaceById(placeId);

        if (place == null) throw new ApiException("Place not found");
        if (!place.getMeetingId().equals(meetingId)) throw new ApiException("Place does not belong to this meeting");

        Vote vote = voteRepository.findVoteByParticipantId(participantId);

        if (vote == null) throw new ApiException("Vote not found");
        if (vote.getPlaceId().equals(placeId)) throw new ApiException("Participant already voted for this place");

        vote.setPlaceId(placeId);

        voteRepository.save(vote);
    }

    public List<Vote> getVotesForPlace(Integer placeId) {
        return voteRepository.findVotesByPlaceId(placeId);
    }

    public Map<String, Object> getVotingResult(Integer meetingId) {
        Meeting meeting = meetingRepository.findMeetingById(meetingId);

        if (meeting == null) throw new ApiException("Meeting not found");

        long totalParticipants = participantRepository.countParticipantsByMeetingId(meetingId);

        List<Place> places = placeRepository.findPlacesByMeetingId(meetingId);

        long totalVotes = 0;

        List<Map<String, Object>> placeResults = new ArrayList<>();

        for (Place place : places) {
            long voteCount = voteRepository.countVotesByPlaceId(place.getId());

            totalVotes += voteCount;

            Map<String, Object> placeResult = new HashMap<>();

            placeResult.put("placeId", place.getId());
            placeResult.put("name", place.getName());
            placeResult.put("voteCount", voteCount);

            placeResults.add(placeResult);
        }

        for (Map<String, Object> result : placeResults) {
            long voteCount = (Long) result.get("voteCount");

            double percentage = 0;

            if (totalVotes > 0) percentage = ((double) voteCount / totalVotes) * 100;

            percentage = Math.round(percentage * 100.0) / 100.0;

            result.put("votePercentage", percentage);
        }

        placeResults.sort((result1, result2) -> Long.compare((Long) result2.get("voteCount"), (Long) result1.get("voteCount")));

        Map<String, Object> result = new HashMap<>();

        result.put("totalParticipants", totalParticipants);
        result.put("totalVotes", totalVotes);
        result.put("remainingVotes", totalParticipants - totalVotes);
        result.put("allParticipantsVoted", totalParticipants > 0 && totalParticipants == totalVotes);
        result.put("places", placeResults);

        return result;
    }
}