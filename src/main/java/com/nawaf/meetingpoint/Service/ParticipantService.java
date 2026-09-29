package com.nawaf.meetingpoint.Service;

import com.nawaf.meetingpoint.Api.ApiException;
import com.nawaf.meetingpoint.Model.Meeting;
import com.nawaf.meetingpoint.Model.MeetingRequest;
import com.nawaf.meetingpoint.Model.MeetingRequestMember;
import com.nawaf.meetingpoint.Model.Participant;
import com.nawaf.meetingpoint.Model.User;
import com.nawaf.meetingpoint.Repository.MeetingRepository;
import com.nawaf.meetingpoint.Repository.MeetingRequestMemberRepository;
import com.nawaf.meetingpoint.Repository.MeetingRequestRepository;
import com.nawaf.meetingpoint.Repository.ParticipantRepository;
import com.nawaf.meetingpoint.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final MeetingRepository meetingRepository;
    private final MeetingRequestRepository meetingRequestRepository;
    private final MeetingRequestMemberRepository meetingRequestMemberRepository;
    private final UserRepository userRepository;

    public List<Participant> getParticipants(Integer meetingId) {
        return participantRepository.findParticipantsByMeetingId(meetingId);
    }

    // 0 = Participant added successfully
    // 1 = Meeting not found
    // 2 = Meeting is not open
    // 3 = User is not the organizer
    // 4 = User to add not found
    // 5 = User is already a participant
    // 6 = User was not accepted to the meeting request
    // 7 = Meeting request not found
    public void createParticipant(Integer meetingId, Integer organizerId, Integer userId) {
        Meeting meeting = meetingRepository.findMeetingById(meetingId);

        if (meeting == null) throw new ApiException("Meeting not found");
        if (!meeting.getStatus().equalsIgnoreCase("OPEN")) throw new ApiException("Meeting is not open");

        MeetingRequest meetingRequest = meetingRequestRepository.findMeetingRequestById(meeting.getMeetingRequestId());

        if (meetingRequest == null) throw new ApiException("Meeting request not found");
        if (!meetingRequest.getOrganizerId().equals(organizerId)) throw new ApiException("User is not the organizer");

        User user = userRepository.findUserById(userId);

        if (user == null) throw new ApiException("User to add not found");

        Participant oldParticipant = participantRepository.findParticipantByMeetingIdAndUserId(meetingId, userId);

        if (oldParticipant != null) throw new ApiException("User is already a participant");

        if (!meetingRequest.getOrganizerId().equals(userId)) {
            MeetingRequestMember member = meetingRequestMemberRepository.findMeetingRequestMemberByMeetingRequestIdAndUserId(meetingRequest.getId(), userId);

            if (member == null || !member.getStatus().equalsIgnoreCase("ACCEPTED")) throw new ApiException("User was not accepted to the meeting request");
        }

        Participant participant = new Participant();

        participant.setMeetingId(meetingId);
        participant.setUserId(userId);

        participantRepository.save(participant);
    }

    // 0 = Participant removed successfully
    // 1 = Meeting not found
    // 2 = Meeting is not open
    // 3 = User is not the organizer
    // 4 = Participant not found
    // 5 = Participant does not belong to this meeting
    // 6 = Organizer cannot be removed from the meeting
    // 7 = Meeting request not found
    public void removeParticipant(Integer meetingId, Integer organizerId, Integer participantId) {
        Meeting meeting = meetingRepository.findMeetingById(meetingId);

        if (meeting == null) throw new ApiException("Meeting not found");
        if (!meeting.getStatus().equalsIgnoreCase("OPEN")) throw new ApiException("Meeting is not open");

        MeetingRequest meetingRequest = meetingRequestRepository.findMeetingRequestById(meeting.getMeetingRequestId());

        if (meetingRequest == null) throw new ApiException("Meeting request not found");
        if (!meetingRequest.getOrganizerId().equals(organizerId)) throw new ApiException("User is not the organizer");

        Participant participant = participantRepository.findParticipantById(participantId);

        if (participant == null) throw new ApiException("User to add not found");
        if (!participant.getMeetingId().equals(meetingId)) throw new ApiException("User is already a participant");
        if (participant.getUserId().equals(meetingRequest.getOrganizerId())) throw new ApiException("User was not accepted to the meeting request");

        participantRepository.delete(participant);

    }

    public Participant getParticipant(Integer id) {
        Participant participant = participantRepository.findParticipantById(id);

        if (participant == null) throw new ApiException("Participant not found");

        return participant;
    }

    // 0 = Location updated successfully
    // 1 = Participant not found
    // 2 = User does not own this participant
    // 3 = Invalid latitude
    // 4 = Invalid longitude
    // 5 = Meeting not found
    // 6 = Meeting does not allow location updates
    public void updateLocation(Integer participantId, Integer userId, Double latitude, Double longitude) {
        Participant participant = participantRepository.findParticipantById(participantId);

        if (participant == null) throw new ApiException("Participant not found");
        if (!participant.getUserId().equals(userId)) throw new ApiException("User does not own this participant");
        if (latitude < -90 || latitude > 90) throw new ApiException("Invalid latitude");
        if (longitude < -180 || longitude > 180) throw new ApiException("Invalid longitude");

        Meeting meeting = meetingRepository.findMeetingById(participant.getMeetingId());

        if (meeting == null) throw new ApiException("Meeting not found");
        if (!meeting.getStatus().equalsIgnoreCase("OPEN")) throw new ApiException("Meeting does not allow location updates");

        participant.setLatitude(latitude);
        participant.setLongitude(longitude);

        participantRepository.save(participant);
    }
}