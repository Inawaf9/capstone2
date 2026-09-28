package com.nawaf.meetingpoint.Service;

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
    public int createParticipant(Integer meetingId, Integer organizerId, Integer userId) {
        Meeting meeting = meetingRepository.findMeetingById(meetingId);

        if (meeting == null) return 1;
        if (!meeting.getStatus().equalsIgnoreCase("OPEN")) return 2;

        MeetingRequest meetingRequest = meetingRequestRepository.findMeetingRequestById(meeting.getMeetingRequestId());

        if (meetingRequest == null) return 7;
        if (!meetingRequest.getOrganizerId().equals(organizerId)) return 3;

        User user = userRepository.findUserById(userId);

        if (user == null) return 4;

        Participant oldParticipant = participantRepository.findParticipantByMeetingIdAndUserId(meetingId, userId);

        if (oldParticipant != null) return 5;

        if (!meetingRequest.getOrganizerId().equals(userId)) {
            MeetingRequestMember member = meetingRequestMemberRepository.findMeetingRequestMemberByMeetingRequestIdAndUserId(meetingRequest.getId(), userId);

            if (member == null || !member.getStatus().equalsIgnoreCase("ACCEPTED")) return 6;
        }

        Participant participant = new Participant();

        participant.setMeetingId(meetingId);
        participant.setUserId(userId);

        participantRepository.save(participant);

        return 0;
    }

    // 0 = Participant removed successfully
    // 1 = Meeting not found
    // 2 = Meeting is not open
    // 3 = User is not the organizer
    // 4 = Participant not found
    // 5 = Participant does not belong to this meeting
    // 6 = Organizer cannot be removed from the meeting
    // 7 = Meeting request not found
    public int removeParticipant(Integer meetingId, Integer organizerId, Integer participantId) {
        Meeting meeting = meetingRepository.findMeetingById(meetingId);

        if (meeting == null) return 1;
        if (!meeting.getStatus().equalsIgnoreCase("OPEN")) return 2;

        MeetingRequest meetingRequest = meetingRequestRepository.findMeetingRequestById(meeting.getMeetingRequestId());

        if (meetingRequest == null) return 7;
        if (!meetingRequest.getOrganizerId().equals(organizerId)) return 3;

        Participant participant = participantRepository.findParticipantById(participantId);

        if (participant == null) return 4;
        if (!participant.getMeetingId().equals(meetingId)) return 5;
        if (participant.getUserId().equals(meetingRequest.getOrganizerId())) return 6;

        participantRepository.delete(participant);

        return 0;
    }

    public Participant getParticipant(Integer id) {
        return participantRepository.findParticipantById(id);
    }

    // 0 = Location updated successfully
    // 1 = Participant not found
    // 2 = User does not own this participant
    // 3 = Invalid latitude
    // 4 = Invalid longitude
    // 5 = Meeting not found
    // 6 = Meeting does not allow location updates
    public int updateLocation(Integer participantId, Integer userId, Double latitude, Double longitude) {
        Participant participant = participantRepository.findParticipantById(participantId);

        if (participant == null) return 1;
        if (!participant.getUserId().equals(userId)) return 2;
        if (latitude < -90 || latitude > 90) return 3;
        if (longitude < -180 || longitude > 180) return 4;

        Meeting meeting = meetingRepository.findMeetingById(participant.getMeetingId());

        if (meeting == null) return 5;
        if (!meeting.getStatus().equalsIgnoreCase("OPEN")) return 6;

        participant.setLatitude(latitude);
        participant.setLongitude(longitude);

        participantRepository.save(participant);

        return 0;
    }
}