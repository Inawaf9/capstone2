package com.nawaf.meetingpoint.Service;

import com.nawaf.meetingpoint.Model.MeetingRequest;
import com.nawaf.meetingpoint.Model.User;
import com.nawaf.meetingpoint.Repository.MeetingRequestRepository;
import com.nawaf.meetingpoint.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
    public int createMeetingRequest(MeetingRequest meetingRequest) {
        User organizer = userRepository.findUserById(meetingRequest.getOrganizerId());

        if (organizer == null) return 1;

        meetingRequest.setCategory(meetingRequest.getCategory().toLowerCase());
        meetingRequest.setStatus("PENDING");
        meetingRequest.setInviteCode(null);

        meetingRequestRepository.save(meetingRequest);
        return 0;
    }

    // 0 = Meeting request updated successfully
    // 1 = Meeting request not found
    // 2 = User is not the organizer
    // 3 = Meeting request already started
    // 4 = Meeting request is canceled
    public int updateMeetingRequest(Integer id, Integer userId, MeetingRequest updatedMeetingRequest) {
        MeetingRequest oldMeetingRequest = meetingRequestRepository.findMeetingRequestById(id);

        if (oldMeetingRequest == null) return 1;
        if (!oldMeetingRequest.getOrganizerId().equals(userId)) return 2;
        if (oldMeetingRequest.getStatus().equalsIgnoreCase("READY")) return 3;
        if (oldMeetingRequest.getStatus().equalsIgnoreCase("CANCELLED")) return 4;

        oldMeetingRequest.setName(updatedMeetingRequest.getName());
        oldMeetingRequest.setCategory(updatedMeetingRequest.getCategory().toLowerCase());
        oldMeetingRequest.setMeetingTime(updatedMeetingRequest.getMeetingTime());

        meetingRequestRepository.save(oldMeetingRequest);
        return 0;
    }

    // 0 = Meeting request deleted successfully
    // 1 = Meeting request not found
    // 2 = User is not the organizer
    // 3 = Meeting request already started
    public int deleteMeetingRequest(Integer id, Integer userId) {
        MeetingRequest meetingRequest = meetingRequestRepository.findMeetingRequestById(id);

        if (meetingRequest == null) return 1;
        if (!meetingRequest.getOrganizerId().equals(userId)) return 2;
        if (meetingRequest.getStatus().equalsIgnoreCase("READY")) return 3;

        meetingRequestRepository.delete(meetingRequest);
        return 0;
    }

    public MeetingRequest getMeetingRequest(Integer id) {
        return meetingRequestRepository.findMeetingRequestById(id);
    }

    // 0 = Meeting request canceled successfully
    // 1 = Meeting request not found
    // 2 = User is not the organizer
    // 3 = Meeting request is already canceled
    // 4 = Meeting request already started
    public int cancelMeetingRequest(Integer id, Integer userId) {
        MeetingRequest meetingRequest = meetingRequestRepository.findMeetingRequestById(id);

        if (meetingRequest == null) return 1;
        if (!meetingRequest.getOrganizerId().equals(userId)) return 2;
        if (meetingRequest.getStatus().equalsIgnoreCase("CANCELLED")) return 3;
        if (meetingRequest.getStatus().equalsIgnoreCase("READY")) return 4;

        meetingRequest.setStatus("CANCELLED");

        meetingRequestRepository.save(meetingRequest);
        return 0;
    }
}