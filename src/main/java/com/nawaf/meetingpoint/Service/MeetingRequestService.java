package com.nawaf.meetingpoint.Service;

import com.nawaf.meetingpoint.Api.ApiException;
import com.nawaf.meetingpoint.Api.ApiResponse;
import com.nawaf.meetingpoint.Model.MeetingRequest;
import com.nawaf.meetingpoint.Model.User;
import com.nawaf.meetingpoint.Repository.MeetingRequestRepository;
import com.nawaf.meetingpoint.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingRequestService {

    private final MeetingRequestRepository meetingRequestRepository;
    private final UserRepository userRepository;

    public List<MeetingRequest> getMeetingRequests() {
        return meetingRequestRepository.findAll();
    }

    // 0 = Meeting request created successfully
    // 1 = Organizer not found
    public void createMeetingRequest(MeetingRequest meetingRequest) {
        User organizer = userRepository.findUserById(meetingRequest.getOrganizerId());

        if (organizer == null) throw new ApiException("Organizer not found");

        meetingRequest.setCategory(meetingRequest.getCategory().toLowerCase());
        meetingRequest.setStatus("PENDING");
        meetingRequest.setInviteCode(null);

        meetingRequestRepository.save(meetingRequest);
    }

    // 0 = Meeting request updated successfully
    // 1 = Meeting request not found
    // 2 = User is not the organizer
    // 3 = Meeting request already started
    // 4 = Meeting request is canceled
    public void updateMeetingRequest(Integer id, Integer userId, MeetingRequest updatedMeetingRequest) {
        MeetingRequest oldMeetingRequest = meetingRequestRepository.findMeetingRequestById(id);

        if (oldMeetingRequest == null) throw new ApiException("Meeting request not found");
        if (!oldMeetingRequest.getOrganizerId().equals(userId)) throw new ApiException("User is not the organizer");
        if (oldMeetingRequest.getStatus().equalsIgnoreCase("READY")) throw new ApiException("Meeting request already started");
        if (oldMeetingRequest.getStatus().equalsIgnoreCase("CANCELLED")) throw new ApiException("Meeting request is canceled");

        oldMeetingRequest.setName(updatedMeetingRequest.getName());
        oldMeetingRequest.setCategory(updatedMeetingRequest.getCategory().toLowerCase());
        oldMeetingRequest.setMeetingTime(updatedMeetingRequest.getMeetingTime());

        meetingRequestRepository.save(oldMeetingRequest);
    }

    // 0 = Meeting request deleted successfully
    // 1 = Meeting request not found
    // 2 = User is not the organizer
    // 3 = Meeting request already started
    public void deleteMeetingRequest(Integer id, Integer userId) {
        MeetingRequest meetingRequest = meetingRequestRepository.findMeetingRequestById(id);

        if (meetingRequest == null) throw new ApiException("Meeting request not found");
        if (!meetingRequest.getOrganizerId().equals(userId)) throw new ApiException("User is not the organizer");
        if (meetingRequest.getStatus().equalsIgnoreCase("READY")) throw new ApiException("Meeting request already started");

        meetingRequestRepository.delete(meetingRequest);
    }

    public MeetingRequest getMeetingRequest(Integer id) {
        MeetingRequest meetingRequest = meetingRequestRepository.findMeetingRequestById(id);

        if (meetingRequest == null) throw new ApiException("Meeting request not found");

        return meetingRequestRepository.findMeetingRequestById(id);
    }

    // 0 = Meeting request canceled successfully
    // 1 = Meeting request not found
    // 2 = User is not the organizer
    // 3 = Meeting request is already canceled
    // 4 = Meeting request already started
    public void cancelMeetingRequest(Integer id, Integer userId) {
        MeetingRequest meetingRequest = meetingRequestRepository.findMeetingRequestById(id);

        if (meetingRequest == null) throw new ApiException("Meeting request not found");
        if (!meetingRequest.getOrganizerId().equals(userId)) throw new ApiException("User is not the organizer");
        if (meetingRequest.getStatus().equalsIgnoreCase("CANCELLED")) throw new ApiException("Meeting request already canceled");
        if (meetingRequest.getStatus().equalsIgnoreCase("READY")) throw new ApiException("Meeting request already started");

        meetingRequest.setStatus("CANCELLED");

        meetingRequestRepository.save(meetingRequest);
    }
}