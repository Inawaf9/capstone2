package com.nawaf.meetingpoint.Service;

import com.nawaf.meetingpoint.Api.ApiException;
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
    // 3 = Meeting request is canceled
    // 4 = Meeting request already started
    // 5 = Invite code already exists
    public void generateInviteCode(Integer requestId, Integer organizerId) {
        MeetingRequest meetingRequest = meetingRequestRepository.findMeetingRequestById(requestId);

        if (meetingRequest == null) throw new ApiException("Meeting request not found");
        if (!meetingRequest.getOrganizerId().equals(organizerId)) throw new ApiException("User is not the organizer");
        if (meetingRequest.getStatus().equalsIgnoreCase("CANCELLED")) throw new ApiException("Meeting request is canceled");
        if (meetingRequest.getStatus().equalsIgnoreCase("READY")) throw new ApiException("Meeting request already started");
        if (meetingRequest.getInviteCode() != null) throw new ApiException("Invite code already exists");

        meetingRequest.setInviteCode(createUniqueCode());

        meetingRequestRepository.save(meetingRequest);
    }

    // 0 = Invite code regenerated successfully
    // 1 = Meeting request not found
    // 2 = User is not the organizer
    // 3 = Meeting request is canceled
    // 4 = Meeting request already started
    // 5 = Invite code is disabled
    public void regenerateInviteCode(Integer requestId, Integer organizerId) {
        MeetingRequest meetingRequest = meetingRequestRepository.findMeetingRequestById(requestId);

        if (meetingRequest == null) throw new ApiException("Meeting request not found");
        if (!meetingRequest.getOrganizerId().equals(organizerId)) throw new ApiException("User is not the organizer");
        if (meetingRequest.getStatus().equalsIgnoreCase("CANCELLED")) throw new ApiException("Meeting request is canceled");
        if (meetingRequest.getStatus().equalsIgnoreCase("READY")) throw new ApiException("Meeting request already started");
        if (meetingRequest.getInviteCode() == null) throw new ApiException("Invite code is disabled");

        meetingRequest.setInviteCode(createUniqueCode());

        meetingRequestRepository.save(meetingRequest);
    }

    // 0 = Invite code disabled successfully
    // 1 = Meeting request not found
    // 2 = User is not the organizer
    // 3 = Meeting request is canceled
    // 4 = Meeting request already started
    // 5 = Invite code is already disabled
    public void disableInviteCode(Integer requestId, Integer organizerId) {
        MeetingRequest meetingRequest = meetingRequestRepository.findMeetingRequestById(requestId);

        if (meetingRequest == null) throw new ApiException("Meeting request not found");
        if (!meetingRequest.getOrganizerId().equals(organizerId)) throw new ApiException("User is not the organizer");
        if (meetingRequest.getStatus().equalsIgnoreCase("CANCELLED")) throw new ApiException("Meeting request is canceled");
        if (meetingRequest.getStatus().equalsIgnoreCase("READY")) throw new ApiException("Meeting request already started");
        if (meetingRequest.getInviteCode() == null) throw new ApiException("Invite code is disabled");

        meetingRequest.setInviteCode(null);

        meetingRequestRepository.save(meetingRequest);
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