package com.nawaf.meetingpoint.Service;

import com.nawaf.meetingpoint.Model.MeetingRequest;
import com.nawaf.meetingpoint.Repository.MeetingRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InviteCodeService {

    private final MeetingRequestRepository meetingRequestRepository;

    // 0 = Invite code generated successfully
    // 1 = Meeting request not found
    // 2 = User is not the organizer
    // 3 = Meeting request is cancelled
    // 4 = Meeting request already started
    // 5 = Invite code already exists
    public int generateInviteCode(Integer requestId, Integer organizerId) {
        MeetingRequest meetingRequest = meetingRequestRepository.findMeetingRequestById(requestId);

        if (meetingRequest == null) return 1;
        if (!meetingRequest.getOrganizerId().equals(organizerId)) return 2;
        if (meetingRequest.getStatus().equalsIgnoreCase("CANCELLED")) return 3;
        if (meetingRequest.getStatus().equalsIgnoreCase("READY")) return 4;
        if (meetingRequest.getInviteCode() != null) return 5;

        meetingRequest.setInviteCode(createUniqueCode());

        meetingRequestRepository.save(meetingRequest);

        return 0;
    }

    // 0 = Invite code regenerated successfully
    // 1 = Meeting request not found
    // 2 = User is not the organizer
    // 3 = Meeting request is cancelled
    // 4 = Meeting request already started
    // 5 = Invite code is disabled
    public int regenerateInviteCode(Integer requestId, Integer organizerId) {
        MeetingRequest meetingRequest = meetingRequestRepository.findMeetingRequestById(requestId);

        if (meetingRequest == null) return 1;
        if (!meetingRequest.getOrganizerId().equals(organizerId)) return 2;
        if (meetingRequest.getStatus().equalsIgnoreCase("CANCELLED")) return 3;
        if (meetingRequest.getStatus().equalsIgnoreCase("READY")) return 4;
        if (meetingRequest.getInviteCode() == null) return 5;

        meetingRequest.setInviteCode(createUniqueCode());

        meetingRequestRepository.save(meetingRequest);

        return 0;
    }

    // 0 = Invite code disabled successfully
    // 1 = Meeting request not found
    // 2 = User is not the organizer
    // 3 = Meeting request is cancelled
    // 4 = Meeting request already started
    // 5 = Invite code is already disabled
    public int disableInviteCode(Integer requestId, Integer organizerId) {
        MeetingRequest meetingRequest = meetingRequestRepository.findMeetingRequestById(requestId);

        if (meetingRequest == null) return 1;
        if (!meetingRequest.getOrganizerId().equals(organizerId)) return 2;
        if (meetingRequest.getStatus().equalsIgnoreCase("CANCELLED")) return 3;
        if (meetingRequest.getStatus().equalsIgnoreCase("READY")) return 4;
        if (meetingRequest.getInviteCode() == null) return 5;

        meetingRequest.setInviteCode(null);

        meetingRequestRepository.save(meetingRequest);

        return 0;
    }

    public MeetingRequest getMeetingRequestByInviteCode(String inviteCode) {
        return meetingRequestRepository.findMeetingRequestByInviteCode(inviteCode);
    }

    private String createUniqueCode() {
        String inviteCode;

        do {
            inviteCode = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        } while (meetingRequestRepository.existsMeetingRequestByInviteCode(inviteCode));

        return inviteCode;
    }

    public MeetingRequest getMeetingRequestByInviteCodeForRequest(Integer requestId) {
        return meetingRequestRepository.findMeetingRequestById(requestId);
    }
}