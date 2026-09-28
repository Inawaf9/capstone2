package com.nawaf.meetingpoint.Repository;

import com.nawaf.meetingpoint.Model.MeetingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingRequestRepository  extends JpaRepository<MeetingRequest, Integer> {
    MeetingRequest findMeetingRequestById(Integer id);

    MeetingRequest findMeetingRequestByInviteCode(String inviteCode);

    boolean existsMeetingRequestByInviteCode(String inviteCode);
}
